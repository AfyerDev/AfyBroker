package net.afyer.afybroker.client.service;

import com.alipay.remoting.rpc.exception.InvokeException;
import com.alipay.remoting.serialization.Serializer;
import net.afyer.afybroker.client.BrokerClient;
import net.afyer.afybroker.core.message.RpcInvocationMessage;
import net.afyer.afybroker.core.observability.RpcObservation;
import net.afyer.afybroker.core.observability.RpcPhase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.ref.ReferenceQueue;
import java.lang.ref.SoftReference;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

import static net.afyer.afybroker.core.util.BoltUtils.*;

/**
 * 服务代理工厂
 *
 * @author Nipuru
 * @since 2025/7/11 17:04
 */
public class BrokerServiceProxyFactory {

    private static final Logger LOGGER = LoggerFactory.getLogger(BrokerServiceProxyFactory.class);

    private final BrokerClient brokerClient;
    private final ConcurrentHashMap<String, ProxyReference> proxies = new ConcurrentHashMap<>();
    private final ReferenceQueue<Object> referenceQueue = new ReferenceQueue<>();

    public BrokerServiceProxyFactory(BrokerClient brokerClient) {
        this.brokerClient = brokerClient;
    }

    /**
     * 创建服务代理
     */
    public <T> T createProxy(Class<T> serviceInterface) {
        return createProxy(serviceInterface, null);
    }

    /**
     * 创建服务代理（带标签选择）
     */
    @SuppressWarnings("unchecked")
    public <T> T createProxy(Class<T> serviceInterface, String tag) {
        Objects.requireNonNull(serviceInterface, "serviceInterface");
        if (!serviceInterface.isInterface()) throw new IllegalArgumentException("Service type must be an interface");
        cleanReference();
        String key = serviceKey(serviceInterface.getName(), tag);
        ProxyReference reference = proxies.get(key);
        Object proxy = reference == null ? null : reference.get();
        if (proxy != null) {
            return (T) proxy;
        }
        return (T) createAndCacheProxy(serviceInterface, key);
    }

    private void cleanReference() {
        ProxyReference reference;
        while ((reference = (ProxyReference) referenceQueue.poll()) != null) {
            proxies.remove(reference.key, reference);
        }
    }

    private Object createAndCacheProxy(Class<?> serviceInterface, String key) {
        Object proxy = Proxy.newProxyInstance(
                serviceInterface.getClassLoader(),
                new Class<?>[]{serviceInterface},
                new ServiceInvocationHandler(brokerClient, key)
        );
        proxies.put(key, new ProxyReference(key, proxy, referenceQueue));
        return proxy;
    }

    private static class ProxyReference extends SoftReference<Object> {
        private final String key;

        private ProxyReference(String key, Object proxy, ReferenceQueue<Object> queue) {
            super(proxy, queue);
            this.key = key;
        }
    }

    /**
     * 服务调用处理器
     */
    private static class ServiceInvocationHandler implements InvocationHandler {
        private final BrokerClient brokerClient;
        private final String serviceKey;

        public ServiceInvocationHandler(BrokerClient brokerClient, String serviceKey) {
            this.brokerClient = brokerClient;
            this.serviceKey = serviceKey;
        }

        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            // 处理Object类的方法
            if (method.getDeclaringClass() == Object.class) {
                if (method.getName().equals("equals")) return proxy == args[0];
                if (method.getName().equals("hashCode")) return System.identityHashCode(proxy);
                return method.invoke(this, args);
            }

            args = args != null ? args : new Object[0];
            Serializer serializer = brokerClient.getSerializer();
            byte[] parameters = serializer.serialize(args);
            // 构建调用消息
            RpcInvocationMessage message = new RpcInvocationMessage()
                    .setServiceKey(serviceKey)
                    .setMethodName(method.getName())
                    .setParameterTypes(getParameterTypeNames(method.getParameterTypes()))
                    .setParameters(parameters);

            long startNanos = System.nanoTime();
            boolean success = false;
            try {
                LOGGER.debug("Invoking remote service: {}.{} [tag={}]",
                        serviceInterface(serviceKey), method.getName(), serviceTag(serviceKey));
                byte[] result = brokerClient.invokeSync(message);
                success = true;
                return serializer.deserialize(result, Object.class.getName());
            } catch (Exception e) {
                throw new InvokeException("RPC invocation failed: " + serviceInterface(serviceKey) + "." + method.getName()
                        + " [tag=" + serviceTag(serviceKey) + "]", e);
            } finally {
                brokerClient.getObservability().onRpc(new RpcObservation(
                        RpcPhase.OUTBOUND,
                        serviceKey,
                        method.getName(),
                        success,
                        System.nanoTime() - startNanos
                ));
            }
        }

        private String[] getParameterTypeNames(Class<?>[] parameterTypes) {
            String[] typeNames = new String[parameterTypes.length];
            for (int i = 0; i < parameterTypes.length; i++) {
                typeNames[i] = parameterTypes[i].getName();
            }
            return typeNames;
        }
    }
} 
