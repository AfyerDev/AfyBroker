package net.afyer.afybroker.core.interceptor;

import com.alipay.remoting.InvokeCallback;
import com.alipay.remoting.exception.RemotingException;
import com.alipay.remoting.rpc.RpcResponseFuture;

import java.util.concurrent.Executor;

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

    private Object invokeWithRetry(Invocation invocation, InvocationContext context) throws Throwable {
        Throwable last = null;
        int maxAttempts = maxRetries + 1;
        for (int attempt = 1; attempt <= maxAttempts; attempt++) {
            try {
                return invocation.proceed();
            } catch (Throwable throwable) {
                last = throwable;
                context.setThrowable(throwable);
                if (attempt >= maxAttempts || !retryPredicate.shouldRetry(context, throwable)) {
                    break;
                }
                sleepBeforeRetry();
            }
        }
        return fallbackOrThrow(context, last);
    }

    private Object interceptCallback(Invocation invocation, InvocationContext context) throws Throwable {
        InvokeCallback original = (InvokeCallback) context.getCallback();
        context.setCallback(new RetryingInvokeCallback(original, invocation, context));
        Object result = invokeWithRetry(invocation, context);
        if (fallback != null && context.getThrowable() != null) {
            original.onResponse(result);
        }
        return null;
    }

    private Object interceptFuture(Invocation invocation, InvocationContext context) throws Throwable {
        Throwable last = null;
        int maxAttempts = maxRetries + 1;
        for (int attempt = 1; attempt <= maxAttempts; attempt++) {
            try {
                Object result = invocation.proceed();
                if (result instanceof RpcResponseFuture) {
                    return new RetryingRpcResponseFuture((RpcResponseFuture) result, invocation, context);
                }
                return result;
            } catch (Throwable throwable) {
                last = throwable;
                context.setThrowable(throwable);
                if (attempt >= maxAttempts || !retryPredicate.shouldRetry(context, throwable)) {
                    break;
                }
                sleepBeforeRetry();
            }
        }
        if (fallback != null) {
            return new FallbackRpcResponseFuture(context, last);
        }
        throw last;
    }

    private Object fallbackOrThrow(InvocationContext context, Throwable throwable) throws Throwable {
        if (fallback != null) {
            return fallback.fallback(context, throwable);
        }
        throw throwable;
    }

    private void sleepBeforeRetry() throws InterruptedException {
        if (retryDelayMillis <= 0) {
            return;
        }
        Thread.sleep(retryDelayMillis);
    }

    private class RetryingInvokeCallback implements InvokeCallback {
        private final InvokeCallback delegate;
        private final Invocation invocation;
        private final InvocationContext context;
        private int attempts = 1;

        private RetryingInvokeCallback(InvokeCallback delegate, Invocation invocation, InvocationContext context) {
            this.delegate = delegate;
            this.invocation = invocation;
            this.context = context;
        }

        @Override
        public void onResponse(Object result) {
            delegate.onResponse(result);
        }

        @Override
        public void onException(Throwable e) {
            Throwable failure = e;
            int maxAttempts = maxRetries + 1;
            while (attempts < maxAttempts && retryPredicate.shouldRetry(context, failure)) {
                attempts++;
                context.setThrowable(failure);
                try {
                    sleepBeforeRetry();
                    invocation.proceed();
                    return;
                } catch (Throwable retryFailure) {
                    failure = retryFailure;
                }
            }
            if (fallback != null) {
                try {
                    delegate.onResponse(fallback.fallback(context, failure));
                } catch (Throwable fallbackFailure) {
                    delegate.onException(fallbackFailure);
                }
                return;
            }
            delegate.onException(failure);
        }

        @Override
        public Executor getExecutor() {
            return delegate.getExecutor();
        }
    }

    private class RetryingRpcResponseFuture extends RpcResponseFuture {
        private final Invocation invocation;
        private final InvocationContext context;
        private RpcResponseFuture current;

        private RetryingRpcResponseFuture(RpcResponseFuture current, Invocation invocation, InvocationContext context) {
            super(null, null);
            this.current = current;
            this.invocation = invocation;
            this.context = context;
        }

        @Override
        public boolean isDone() {
            return current.isDone();
        }

        @Override
        public Object get(int timeoutMillis) throws RemotingException, InterruptedException {
            return get0(timeoutMillis);
        }

        @Override
        public Object get() throws RemotingException, InterruptedException {
            return get0(null);
        }

        private Object get0(Integer timeoutMillis) throws RemotingException, InterruptedException {
            Throwable failure = null;
            int maxAttempts = maxRetries + 1;
            for (int attempt = 1; attempt <= maxAttempts; attempt++) {
                try {
                    return timeoutMillis == null ? current.get() : current.get(timeoutMillis);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    throw e;
                } catch (Throwable throwable) {
                    failure = throwable;
                    context.setThrowable(throwable);
                    if (attempt >= maxAttempts || !retryPredicate.shouldRetry(context, throwable)) {
                        break;
                    }
                    try {
                        sleepBeforeRetry();
                        Object result = invocation.proceed();
                        if (!(result instanceof RpcResponseFuture)) {
                            return result;
                        }
                        current = (RpcResponseFuture) result;
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                        throw e;
                    } catch (Throwable retryFailure) {
                        failure = retryFailure;
                    }
                }
            }
            try {
                return fallbackOrThrow(context, failure);
            } catch (RemotingException e) {
                throw e;
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw e;
            } catch (Throwable throwable) {
                throw new RemotingException(throwable.getMessage(), throwable);
            }
        }
    }

    private class FallbackRpcResponseFuture extends RpcResponseFuture {
        private final InvocationContext context;
        private final Throwable failure;

        private FallbackRpcResponseFuture(InvocationContext context, Throwable failure) {
            super(null, null);
            this.context = context;
            this.failure = failure;
        }

        @Override
        public boolean isDone() {
            return true;
        }

        @Override
        public Object get(int timeoutMillis) throws RemotingException, InterruptedException {
            return get();
        }

        @Override
        public Object get() throws RemotingException, InterruptedException {
            try {
                return fallback.fallback(context, failure);
            } catch (RemotingException e) {
                throw e;
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw e;
            } catch (Throwable throwable) {
                throw new RemotingException(throwable.getMessage(), throwable);
            }
        }
    }

    public interface Fallback {
        Object fallback(InvocationContext context, Throwable cause) throws Throwable;
    }

    public interface RetryPredicate {
        boolean shouldRetry(InvocationContext context, Throwable throwable);
    }

}
