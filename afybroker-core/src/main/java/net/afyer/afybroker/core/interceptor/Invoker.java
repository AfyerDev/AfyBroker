package net.afyer.afybroker.core.interceptor;

public interface Invoker {

    Object invoke(InvocationContext context) throws Throwable;
}
