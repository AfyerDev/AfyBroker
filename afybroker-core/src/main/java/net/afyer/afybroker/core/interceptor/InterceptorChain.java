package net.afyer.afybroker.core.interceptor;

import java.util.Collections;
import java.util.List;

public final class InterceptorChain {

    private InterceptorChain() {
    }

    public static Object invoke(List<Interceptor> interceptors,
                                InvocationContext context,
                                Invoker target) throws Throwable {
        List<Interceptor> chain = interceptors == null ? Collections.emptyList() : interceptors;
        try {
            Object response = invokeNext(chain, 0, context, target);
            context.setResponse(response);
            context.setThrowable(null);
            return response;
        } catch (Throwable throwable) {
            context.setThrowable(throwable);
            throw throwable;
        }
    }

    private static Object invokeNext(final List<Interceptor> interceptors,
                                     final int index,
                                     final InvocationContext context,
                                     final Invoker target) throws Throwable {
        if (index >= interceptors.size()) {
            return target.invoke(context);
        }
        return interceptors.get(index).intercept(new Invocation() {
            @Override
            public InvocationContext getContext() {
                return context;
            }

            @Override
            public Object proceed() throws Throwable {
                return invokeNext(interceptors, index + 1, context, target);
            }
        });
    }
}
