package net.afyer.afybroker.client.service;

import net.afyer.afybroker.core.BrokerServiceDescriptor;

import java.lang.reflect.Method;
import java.util.*;

/**
 * @author Nipuru
 * @since 2025/07/12 11:53
 */
public class BrokerServiceEntry {
    private final Class<?> serviceInterface;
    private final Object serviceImpl;
    private final Set<String> tags;
    private final Map<MethodKey, Method> methodCache;

    public BrokerServiceEntry(Class<?> serviceInterface, Object serviceImpl, Set<String> tags) {
        this.serviceInterface = serviceInterface;
        this.serviceImpl = serviceImpl;
        this.tags = tags;
        this.methodCache = new HashMap<>();

        // 预先缓存所有方法
        cacheAllMethods();
    }

    public Class<?> getServiceInterface() {
        return serviceInterface;
    }

    public Object getServiceImpl() {
        return serviceImpl;
    }

    public Set<String> getTags() {
        return tags;
    }

    public Map<MethodKey, Method> getMethodCache() {
        return methodCache;
    }

    public Method getMethod(String methodName, String[] parameterTypeNames) {
        MethodKey key = new MethodKey(methodName, parameterTypeNames);
        return methodCache.get(key);
    }

    public BrokerServiceDescriptor getDescriptor() {
        return new BrokerServiceDescriptor()
                .setServiceInterface(serviceInterface.getName())
                .setTags(tags);
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

        public String getMethodName() {
            return methodName;
        }

        public String[] getParameterTypeNames() {
            return parameterTypeNames;
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
