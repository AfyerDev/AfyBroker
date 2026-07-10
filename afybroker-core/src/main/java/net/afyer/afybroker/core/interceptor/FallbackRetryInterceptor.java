package net.afyer.afybroker.core.interceptor;

import com.alipay.remoting.InvokeCallback;
import com.alipay.remoting.RejectedExecutionPolicy;
import com.alipay.remoting.RejectionProcessableInvokeCallback;
import com.alipay.remoting.exception.RemotingException;
import com.alipay.remoting.rpc.RpcResponseFuture;

import java.util.concurrent.Executor;

import static net.afyer.afybroker.core.util.ThrowableUtils.throwUnchecked;

public class FallbackRetryInterceptor implements Interceptor {

    private final int maxRetries;
    private final Fallback fallback;
    private long retryDelayMillis;
    private RetryPredicate retryPredicate = new RetryPredicate() {
        @Override
        public boolean shouldRetry(InvocationContext context, Throwable throwable) {
            return !(throwable instanceof InterruptedException) && !(throwable instanceof Error);
        }
    };

    public FallbackRetryInterceptor(int maxRetries) {
        this(maxRetries, null);
    }

    public FallbackRetryInterceptor(int maxRetries, Fallback fallback) {
        if (maxRetries < 0) {
            throw new IllegalArgumentException("maxRetries must be >= 0");
        }
        this.maxRetries = maxRetries;
        this.fallback = fallback;
    }

    public FallbackRetryInterceptor setRetryDelayMillis(long retryDelayMillis) {
        if (retryDelayMillis < 0) {
            throw new IllegalArgumentException("retryDelayMillis must be >= 0");
        }
        this.retryDelayMillis = retryDelayMillis;
        return this;
    }

    public FallbackRetryInterceptor setRetryPredicate(RetryPredicate retryPredicate) {
        if (retryPredicate == null) {
            throw new NullPointerException("retryPredicate");
        }
        this.retryPredicate = retryPredicate;
        return this;
    }

    @Override
    public Object intercept(Invocation invocation) throws Throwable {
        InvocationContext context = invocation.getContext();
        if (context.getType() == InvocationType.CALLBACK && context.getCallback() instanceof InvokeCallback) {
            return interceptCallback(invocation, context);
        }
        if (context.getType() == InvocationType.FUTURE) {
            return interceptFuture(invocation, context);
        }
        return invokeWithRetry(invocation, context);
    }

    private Object invokeWithRetry(Invocation invocation, InvocationContext context)
            throws Throwable {
        RetryState state = new RetryState();
        state.startAttempt();
        Throwable failure;
        while (true) {
            try {
                Object result = invocation.proceed();
                context.setThrowable(null);
                return result;
            } catch (Throwable throwable) {
                failure = throwable;
                context.setThrowable(throwable);
            }
            if (!shouldRetry(context, failure, state)) {
                return fallbackOrThrow(context, failure);
            }
            sleepBeforeRetry();
            state.startAttempt();
        }
    }

    private Object interceptCallback(Invocation invocation, InvocationContext context) {
        InvokeCallback original = (InvokeCallback) context.getCallback();
        RetryState state = new RetryState();
        long generation = state.startAttempt();
        Throwable failure;
        while (true) {
            context.setCallback(createRetryingCallback(original, invocation, context, state, generation));
            try {
                invocation.proceed();
                if (!state.isCompleted()) {
                    context.setThrowable(null);
                }
                return null;
            } catch (Throwable throwable) {
                if (state.isCompleted()) {
                    return null;
                }
                failure = throwable;
                context.setThrowable(throwable);
            }
            boolean retry;
            try {
                retry = shouldRetry(context, failure, state, generation);
            } catch (Throwable predicateFailure) {
                return completeCallbackOrThrow(context, state, generation, predicateFailure);
            }
            if (!retry) {
                if (!state.complete(generation)) {
                    return null;
                }
                Object result = fallbackOrThrow(context, failure);
                original.onResponse(result);
                return null;
            }
            try {
                sleepBeforeRetry();
            } catch (InterruptedException delayFailure) {
                return completeCallbackOrThrow(context, state, generation, delayFailure);
            }
            long nextGeneration = state.startNextAttempt(generation);
            if (nextGeneration == 0) {
                return null;
            }
            generation = nextGeneration;
        }
    }

    private Object completeCallbackOrThrow(InvocationContext context, RetryState state,
                                           long generation, Throwable failure) {
        context.setThrowable(failure);
        if (state.complete(generation)) {
            return throwUnchecked(failure);
        }
        if (failure instanceof InterruptedException) {
            Thread.currentThread().interrupt();
        }
        return null;
    }

    private InvokeCallback createRetryingCallback(InvokeCallback delegate, Invocation invocation,
                                                   InvocationContext context, RetryState state,
                                                   long generation) {
        if (delegate instanceof RejectionProcessableInvokeCallback) {
            return new RejectionProcessableRetryingInvokeCallback(
                    (RejectionProcessableInvokeCallback) delegate, invocation, context, state, generation);
        }
        return new RetryingInvokeCallback(delegate, invocation, context, state, generation);
    }

    private Object interceptFuture(Invocation invocation, InvocationContext context)
            throws Throwable {
        RetryState state = new RetryState();
        state.startAttempt();
        Throwable failure;
        while (true) {
            try {
                Object result = invocation.proceed();
                context.setThrowable(null);
                if (result instanceof RpcResponseFuture) {
                    return new RetryingRpcResponseFuture(
                            (RpcResponseFuture) result, null, invocation, context, state);
                }
                return result;
            } catch (Throwable throwable) {
                failure = throwable;
                context.setThrowable(throwable);
            }
            if (!shouldRetry(context, failure, state)) {
                if (fallback != null && !isNonRecoverable(failure)) {
                    return new RetryingRpcResponseFuture(null, failure, invocation, context, state);
                }
                return throwUnchecked(failure);
            }
            sleepBeforeRetry();
            state.startAttempt();
        }
    }

    private boolean shouldRetry(InvocationContext context, Throwable failure, RetryState state) {
        return !isNonRecoverable(failure)
                && state.hasRemainingAttempts()
                && retryPredicate.shouldRetry(context, failure);
    }

    private boolean shouldRetry(InvocationContext context, Throwable failure, RetryState state,
                                long generation) {
        return !isNonRecoverable(failure)
                && state.canRetry(generation)
                && retryPredicate.shouldRetry(context, failure);
    }

    private static boolean isNonRecoverable(Throwable failure) {
        return failure instanceof InterruptedException || failure instanceof Error;
    }

    private Object fallbackOrThrow(InvocationContext context, Throwable failure) {
        if (fallback != null && !isNonRecoverable(failure)) {
            try {
                return fallback.fallback(context, failure);
            } catch (Throwable fallbackFailure) {
                return throwUnchecked(fallbackFailure);
            }
        }
        return throwUnchecked(failure);
    }

    private void sleepBeforeRetry() throws InterruptedException {
        if (retryDelayMillis > 0) {
            Thread.sleep(retryDelayMillis);
        }
    }

    private class RetryingInvokeCallback implements InvokeCallback {
        private final InvokeCallback delegate;
        private final Invocation invocation;
        private final InvocationContext context;
        private final RetryState state;
        private final long generation;

        private RetryingInvokeCallback(InvokeCallback delegate, Invocation invocation,
                                       InvocationContext context, RetryState state, long generation) {
            this.delegate = delegate;
            this.invocation = invocation;
            this.context = context;
            this.state = state;
            this.generation = generation;
        }

        @Override
        public void onResponse(Object result) {
            if (!state.complete(generation)) {
                return;
            }
            context.setThrowable(null);
            delegate.onResponse(result);
        }

        @Override
        public void onException(Throwable throwable) {
            long failedGeneration = generation;
            Throwable failure = throwable;
            while (true) {
                if (!state.isCurrent(failedGeneration)) {
                    return;
                }

                context.setThrowable(failure);
                boolean retry;
                try {
                    retry = shouldRetry(context, failure, state, failedGeneration);
                } catch (Throwable predicateFailure) {
                    context.setThrowable(predicateFailure);
                    completeWithFailure(predicateFailure, false, failedGeneration);
                    return;
                }
                if (!retry) {
                    completeWithFailure(failure, true, failedGeneration);
                    return;
                }

                try {
                    sleepBeforeRetry();
                } catch (Throwable delayFailure) {
                    failure = delayFailure;
                    continue;
                }

                long nextGeneration = state.startNextAttempt(failedGeneration);
                if (nextGeneration == 0) {
                    return;
                }
                failedGeneration = nextGeneration;
                context.setCallback(createRetryingCallback(
                        delegate, invocation, context, state, failedGeneration));

                try {
                    invocation.proceed();
                    if (state.isCurrent(failedGeneration)) {
                        context.setThrowable(null);
                    }
                    return;
                } catch (Throwable retryFailure) {
                    if (!state.isCurrent(failedGeneration)) {
                        return;
                    }
                    failure = retryFailure;
                }
            }
        }

        private void completeWithFailure(Throwable failure, boolean allowFallback,
                                         long failedGeneration) {
            if (!state.complete(failedGeneration)) {
                if (failure instanceof InterruptedException) {
                    Thread.currentThread().interrupt();
                }
                return;
            }

            Throwable terminal = failure;
            if (allowFallback && fallback != null && !isNonRecoverable(failure)) {
                try {
                    Object result = fallback.fallback(context, failure);
                    context.setThrowable(null);
                    delegate.onResponse(result);
                    return;
                } catch (Throwable throwable) {
                    terminal = throwable;
                }
            }

            context.setThrowable(terminal);
            delegate.onException(terminal);
            if (terminal instanceof InterruptedException) {
                Thread.currentThread().interrupt();
            }
        }

        @Override
        public Executor getExecutor() {
            return delegate.getExecutor();
        }
    }

    private final class RejectionProcessableRetryingInvokeCallback extends RetryingInvokeCallback
            implements RejectionProcessableInvokeCallback {
        private final RejectionProcessableInvokeCallback delegate;

        private RejectionProcessableRetryingInvokeCallback(RejectionProcessableInvokeCallback delegate,
                                                            Invocation invocation, InvocationContext context,
                                                            RetryState state, long generation) {
            super(delegate, invocation, context, state, generation);
            this.delegate = delegate;
        }

        @Override
        public RejectedExecutionPolicy rejectedExecutionPolicy() {
            return delegate.rejectedExecutionPolicy();
        }
    }

    private class RetryingRpcResponseFuture extends RpcResponseFuture {
        private final Invocation invocation;
        private final InvocationContext context;
        private final RetryState state;
        private volatile RpcResponseFuture current;
        private volatile Throwable pendingFailure;
        private volatile boolean completed;
        private Object terminalResult;
        private Throwable terminalFailure;

        private RetryingRpcResponseFuture(RpcResponseFuture current, Throwable pendingFailure,
                                          Invocation invocation, InvocationContext context,
                                          RetryState state) {
            super(null, null);
            this.current = current;
            this.pendingFailure = pendingFailure;
            this.invocation = invocation;
            this.context = context;
            this.state = state;
        }

        @Override
        public boolean isDone() {
            if (completed) {
                return true;
            }
            if (state.hasRemainingAttempts()) {
                return false;
            }
            RpcResponseFuture snapshot = current;
            if (snapshot != null) {
                return snapshot.isDone();
            }
            return pendingFailure != null && !state.hasRemainingAttempts();
        }

        @Override
        public Object get(int timeoutMillis) {
            return get0(timeoutMillis);
        }

        @Override
        public Object get() {
            return get0(null);
        }

        private synchronized Object get0(Integer timeoutMillis) {
            if (completed) {
                return replayTerminal();
            }

            Throwable failure = pendingFailure;
            while (true) {
                RpcResponseFuture snapshot = current;
                if (snapshot != null) {
                    try {
                        Object result = timeoutMillis == null
                                ? snapshot.get()
                                : snapshot.get(timeoutMillis);
                        return completeSuccessfully(result);
                    } catch (Throwable throwable) {
                        failure = throwable;
                        current = null;
                        pendingFailure = throwable;
                        context.setThrowable(throwable);
                    }
                }

                boolean retry;
                try {
                    retry = shouldRetry(context, failure, state);
                } catch (Throwable predicateFailure) {
                    context.setThrowable(predicateFailure);
                    return completeWithFailure(predicateFailure, false);
                }
                if (!retry) {
                    return completeWithFailure(failure, true);
                }

                try {
                    sleepBeforeRetry();
                    state.startAttempt();
                    Object result = invocation.proceed();
                    context.setThrowable(null);
                    pendingFailure = null;
                    if (result instanceof RpcResponseFuture) {
                        current = (RpcResponseFuture) result;
                    } else {
                        return completeSuccessfully(result);
                    }
                } catch (Throwable retryFailure) {
                    failure = retryFailure;
                    current = null;
                    pendingFailure = retryFailure;
                    context.setThrowable(retryFailure);
                }
            }
        }

        private Object completeSuccessfully(Object result) {
            terminalResult = result;
            terminalFailure = null;
            pendingFailure = null;
            state.complete();
            completed = true;
            context.setThrowable(null);
            return result;
        }

        private Object completeWithFailure(Throwable failure, boolean allowFallback) {
            Throwable terminal = failure;
            if (allowFallback && fallback != null && !isNonRecoverable(failure)) {
                try {
                    return completeSuccessfully(fallback.fallback(context, failure));
                } catch (Throwable fallbackFailure) {
                    terminal = fallbackFailure;
                }
            }

            terminalFailure = terminal;
            pendingFailure = terminal;
            state.complete();
            completed = true;
            context.setThrowable(terminal);
            return replayTerminal();
        }

        private Object replayTerminal() {
            if (terminalFailure == null) {
                return terminalResult;
            }
            if (terminalFailure instanceof InterruptedException) {
                Thread.currentThread().interrupt();
            }
            return throwUnchecked(terminalFailure);
        }
    }

    private final class RetryState {
        private final long maxAttempts = (long) maxRetries + 1;
        private long attempts;
        private boolean completed;

        private synchronized long startAttempt() {
            if (completed || attempts >= maxAttempts) {
                return 0;
            }
            return ++attempts;
        }

        private synchronized long startNextAttempt(long expectedGeneration) {
            if (completed || attempts != expectedGeneration || attempts >= maxAttempts) {
                return 0;
            }
            return ++attempts;
        }

        private synchronized boolean hasRemainingAttempts() {
            return !completed && attempts < maxAttempts;
        }

        private synchronized boolean canRetry(long generation) {
            return !completed && attempts == generation && attempts < maxAttempts;
        }

        private synchronized boolean complete() {
            if (completed) {
                return false;
            }
            completed = true;
            return true;
        }

        private synchronized boolean complete(long generation) {
            if (completed || attempts != generation) {
                return false;
            }
            completed = true;
            return true;
        }

        private synchronized boolean isCurrent(long generation) {
            return !completed && attempts == generation;
        }

        private synchronized boolean isCompleted() {
            return completed;
        }
    }

    public interface Fallback {
        Object fallback(InvocationContext context, Throwable cause) throws Throwable;
    }

    public interface RetryPredicate {
        boolean shouldRetry(InvocationContext context, Throwable throwable);
    }
}
