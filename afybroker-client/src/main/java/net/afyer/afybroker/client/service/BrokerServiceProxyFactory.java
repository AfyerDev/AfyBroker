package net.afyer.afybroker.client.service;

import com.alipay.remoting.rpc.exception.InvokeException;
import com.alipay.remoting.serialization.Serializer;
import net.afyer.afybroker.client.BrokerClient;
import net.afyer.afybroker.core.message.RpcInvocationMessage;
import net.afyer.afybroker.core.observability.RpcObservation;
import net.afyer.afybroker.core.observability.RpcPhase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import net.afyer.afybroker.core.BrokerServiceKey;
import java.util.Objects;

/**
 * 服务代理工厂
 *
 * @author Nipuru
 * @since 2025/7/11 17:04
 */
public class BrokerServiceProxyFactory {

    private static final Logger LOGGER = LoggerFactory.getLogger(BrokerServiceProxyFactory.class);

    private final BrokerClient brokerClient;

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
        new BrokerServiceKey(serviceInterface.getName(), tag);
        return (T) Proxy.newProxyInstance(
                serviceInterface.getClassLoader(),
                new Class[]{serviceInterface},
                new ServiceInvocationHandler(brokerClient, serviceInterface.getName(), tag)
        );
    }

    /**
     * 服务调用处理器
     */
    private static class ServiceInvocationHandler implements InvocationHandler {
        private final BrokerClient brokerClient;
        private final String serviceInterface;
        private final String serviceTag;

        public ServiceInvocationHandler(BrokerClient brokerClient, String serviceInterface, String serviceTag) {
            this.brokerClient = brokerClient;
            this.serviceInterface = serviceInterface;
            this.serviceTag = serviceTag;
        }

        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            // 处理Object类的方法
            if (method.getDeclaringClass() == Object.class) {
                return method.invoke(this, args);
            }

            args = args != null ? args : new Object[0];
            Serializer serializer = brokerClient.getSerializer();
            byte[] parameters = serializer.serialize(args);
            // 构建调用消息
            RpcInvocationMessage message = new RpcInvocationMessage()
                    .setServiceInterface(serviceInterface)
                    .setMethodName(method.getName())
                    .setParameterTypes(getParameterTypeNames(method.getParameterTypes()))
                    .setParameters(parameters)
                    .setServiceTag(serviceTag);

            long startNanos = System.nanoTime();
            boolean success = false;
            try {
                LOGGER.debug("Invoking remote service: {}.{}", serviceInterface, method.getName());
                byte[] result = brokerClient.invokeSync(message);
                success = true;
                return serializer.deserialize(result, Object.class.getName());
            } catch (Exception e) {
                throw new InvokeException("RPC invocation failed: " + serviceInterface + "." + method.getName(), e);
            } finally {
                brokerClient.getObservability().onRpc(new RpcObservation(
                        RpcPhase.OUTBOUND,
                        serviceInterface,
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
