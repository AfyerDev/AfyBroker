package net.afyer.afybroker.client;


import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import net.afyer.afybroker.core.BrokerServiceDescriptor;

import java.lang.reflect.Method;
import java.util.*;

/**
 * @author Nipuru
 * @since 2025/07/12 11:53
 */
@Getter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class BrokerServiceEntry {
    final Class<?> serviceInterface;
    final Object serviceImpl;
    final Set<String> tags;
    final Map<MethodKey, Method> methodCache;

    public BrokerServiceEntry(Class<?> serviceInterface, Object serviceImpl, Set<String> tags) {
        this.serviceInterface = serviceInterface;
        this.serviceImpl = serviceImpl;
        this.tags = tags;
        this.methodCache = new HashMap<>();

        // 预先缓存所有方法
        cacheAllMethods();
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

    public Method getMethod(String methodName, String[] parameterTypeNames) {
        MethodKey key = new MethodKey(methodName, parameterTypeNames);
        return methodCache.get(key);
    }

    public BrokerServiceDescriptor getDescriptor() {
        return new BrokerServiceDescriptor()
                .setServiceInterface(serviceInterface.getName())
                .setTags(tags);
    }

    @Getter
    @FieldDefaults(level = AccessLevel.PRIVATE)
    private static class MethodKey {
        final String methodName;
        final String[] parameterTypeNames;

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
