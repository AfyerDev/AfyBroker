package net.afyer.afybroker.client.service;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;

/**
 * @author Nipuru
 * @since 2025/07/12 11:53
 */
public class BrokerServiceEntry {
    private final Class<?> serviceInterface;
    private final Object serviceImpl;
    private final Map<MethodKey, Method> methodCache;

    public BrokerServiceEntry(Class<?> serviceInterface, Object serviceImpl) {
        this.serviceInterface = serviceInterface;
        this.serviceImpl = serviceImpl;
        this.methodCache = new HashMap<>();

        // 预先缓存所有方法
        cacheAllMethods();
    }

    public Object invoke(String methodName, String[] parameterTypeNames, Object[] parameters) throws Throwable {
        Method method = methodCache.get(new MethodKey(methodName, parameterTypeNames));
        if (method == null) {
            throw new NoSuchMethodException(serviceInterface.getName() + "." + methodName
                    + Arrays.toString(parameterTypeNames));
        }
        try {
            return method.invoke(serviceImpl, parameters);
        } catch (InvocationTargetException e) {
            throw e.getTargetException();
        }
    }

    private void cacheAllMethods() {
        Method[] methods = serviceInterface.getMethods();
        for (Method method : methods) {
            String[] parameterTypeNames = Arrays.stream(method.getParameterTypes())
                    .map(Class::getName)
                    .toArray(String[]::new);
            MethodKey key = new MethodKey(method.getName(), parameterTypeNames);
            methodCache.put(key, method);
        }
    }

    private static class MethodKey {
        private final String methodName;
        private final String[] parameterTypeNames;

        public MethodKey(String methodName, String[] parameterTypeNames) {
            this.methodName = methodName;
            this.parameterTypeNames = parameterTypeNames;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) return true;
            if (obj == null || getClass() != obj.getClass()) return false;
            MethodKey methodKey = (MethodKey) obj;
            return Objects.equals(methodName, methodKey.methodName) &&
                    Arrays.equals(parameterTypeNames, methodKey.parameterTypeNames);
        }

        @Override
        public int hashCode() {
            int result = Objects.hash(methodName);
            result = 31 * result + Arrays.hashCode(parameterTypeNames);
            return result;
        }
    }
}
