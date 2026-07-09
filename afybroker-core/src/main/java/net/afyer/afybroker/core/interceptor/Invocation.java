package net.afyer.afybroker.core.interceptor;

public interface Invocation {

    InvocationContext getContext();

    Object proceed() throws Throwable;
}
