package net.afyer.afybroker.core.interceptor;

public interface Interceptor {

    Object intercept(Invocation invocation) throws Throwable;
}
