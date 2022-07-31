package net.afyer.afybroker.server.plugin;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.FieldDefaults;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * @author Nipuru
 * @since 2022/7/31 11:42
 */
@Getter
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class EventHandlerMethod {
    final Object listener;
    final Method method;

    public void invoke(Object event) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
        method.invoke(listener, event);
    }
}
