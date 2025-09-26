package net.afyer.afybroker.server.plugin;

import com.google.common.collect.ImmutableSet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.MessageFormat;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author Nipuru
 * @since 2022/7/31 11:40
 */
public class EventBus {

    private static final Logger LOGGER = LoggerFactory.getLogger(EventBus.class);

    private final Map<Class<?>, Map<Byte, Map<Object, Method[]>>> byListenerAndPriority = new HashMap<>();
    private final Map<Class<?>, EventHandlerMethod[]> byEventBaked = new ConcurrentHashMap<>();
    private final Lock lock = new ReentrantLock();

    public void post(Object event) {
        EventHandlerMethod[] handlers = byEventBaked.get(event.getClass());

        if (handlers != null) {
            for (EventHandlerMethod method : handlers) {
                long start = System.nanoTime();

                try {
                    method.invoke(event);
                } catch (IllegalAccessException ex) {
                    throw new Error("Method became inaccessible: " + event, ex);
                } catch (IllegalArgumentException ex) {
                    throw new Error("Method rejected target/argument: " + event, ex);
                } catch (InvocationTargetException ex) {
                    LOGGER.warn(MessageFormat.format("Error dispatching event {0} to listener {1}", event, method.getListener()), ex.getCause());
                }

                long elapsed = System.nanoTime() - start;
                if (elapsed > 50000000) {
                    LOGGER.warn("Plugin listener {} took {}ms to process event {}!", method.getListener().getClass().getName(), elapsed / 1000000, event);
                }
            }
        }
    }

    private Map<Class<?>, Map<Byte, Set<Method>>> findHandlers(Object listener) {
        Map<Class<?>, Map<Byte, Set<Method>>> handler = new HashMap<>();
        Set<Method> methods = ImmutableSet.<Method>builder().add(listener.getClass().getMethods()).add(listener.getClass().getDeclaredMethods()).build();
        for (final Method m : methods) {
            EventHandler annotation = m.getAnnotation(EventHandler.class);
            if (annotation != null) {
                Class<?>[] params = m.getParameterTypes();
                if (params.length != 1) {
                    LOGGER.info("Method {} in class {} annotated with {} does not have single argument", m, listener.getClass(), annotation);
                    continue;
                }
                Map<Byte, Set<Method>> prioritiesMap = handler.computeIfAbsent(params[0], k -> new HashMap<>());
                Set<Method> priority = prioritiesMap.computeIfAbsent(annotation.priority(), k -> new HashSet<>());
                priority.add(m);
            }
        }
        return handler;
    }

    public void register(Object listener) {
        Map<Class<?>, Map<Byte, Set<Method>>> handler = findHandlers(listener);
        lock.lock();
        try {
            for (Map.Entry<Class<?>, Map<Byte, Set<Method>>> e : handler.entrySet()) {
                Map<Byte, Map<Object, Method[]>> prioritiesMap = byListenerAndPriority.computeIfAbsent(e.getKey(), k -> new HashMap<>());
                for (Map.Entry<Byte, Set<Method>> entry : e.getValue().entrySet()) {
                    Map<Object, Method[]> currentPriorityMap = prioritiesMap.computeIfAbsent(entry.getKey(), k -> new HashMap<>());
                    currentPriorityMap.put(listener, entry.getValue().toArray(new Method[0]));
                }
                bakeHandlers(e.getKey());
            }
        } finally {
            lock.unlock();
        }
    }

    public void unregister(Object listener) {
        Map<Class<?>, Map<Byte, Set<Method>>> handler = findHandlers(listener);
        lock.lock();
        try {
            for (Map.Entry<Class<?>, Map<Byte, Set<Method>>> e : handler.entrySet()) {
                Map<Byte, Map<Object, Method[]>> prioritiesMap = byListenerAndPriority.get(e.getKey());
                if (prioritiesMap != null) {
                    for (Byte priority : e.getValue().keySet()) {
                        Map<Object, Method[]> currentPriority = prioritiesMap.get(priority);
                        if (currentPriority != null) {
                            currentPriority.remove(listener);
                            if (currentPriority.isEmpty()) {
                                prioritiesMap.remove(priority);
                            }
                        }
                    }
                    if (prioritiesMap.isEmpty()) {
                        byListenerAndPriority.remove(e.getKey());
                    }
                }
                bakeHandlers(e.getKey());
            }
        } finally {
            lock.unlock();
        }
    }

    private void bakeHandlers(Class<?> eventClass) {
        Map<Byte, Map<Object, Method[]>> handlersByPriority = byListenerAndPriority.get(eventClass);
        if (handlersByPriority != null) {
            List<EventHandlerMethod> handlersList = new ArrayList<>(handlersByPriority.size() * 2);

            byte value = Byte.MIN_VALUE;
            do {
                Map<Object, Method[]> handlersByListener = handlersByPriority.get(value);
                if (handlersByListener != null) {
                    for (Map.Entry<Object, Method[]> listenerHandlers : handlersByListener.entrySet()) {
                        for (Method method : listenerHandlers.getValue()) {
                            EventHandlerMethod ehm = new EventHandlerMethod(listenerHandlers.getKey(), method);
                            handlersList.add(ehm);
                        }
                    }
                }
            } while (value++ < Byte.MAX_VALUE);
            byEventBaked.put(eventClass, handlersList.toArray(new EventHandlerMethod[0]));
        } else {
            byEventBaked.remove(eventClass);
        }
    }
}
