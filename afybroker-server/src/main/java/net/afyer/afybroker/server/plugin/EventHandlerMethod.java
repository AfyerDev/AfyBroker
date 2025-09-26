package net.afyer.afybroker.server.plugin;


import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * @author Nipuru
 * @since 2022/7/31 11:42
 */
public class EventHandlerMethod {
    private final Object listener;
    private final Method method;

    public EventHandlerMethod(Object listener, Method method) {
        this.listener = listener;
        this.method = method;
    }

    public Object getListener() {
        return listener;
    }

    public Method getMethod() {
        return method;
    }

    public void invoke(Object event) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
        method.invoke(listener, event);
    }
}
