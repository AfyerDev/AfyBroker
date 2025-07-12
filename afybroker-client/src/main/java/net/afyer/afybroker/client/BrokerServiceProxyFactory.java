package net.afyer.afybroker.client;

import com.alipay.remoting.rpc.exception.InvokeException;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import net.afyer.afybroker.core.message.RpcInvocationMessage;
import net.afyer.afybroker.core.util.HessianSerializer;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Collections;
import java.util.Set;

/**
 * 服务代理工厂
 * 
 * @author Nipuru
 * @since 2025/7/11 17:04
 */
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE)
public class BrokerServiceProxyFactory {
    
    final BrokerClient brokerClient;
    
    public BrokerServiceProxyFactory(BrokerClient brokerClient) {
        this.brokerClient = brokerClient;
    }

    /**
     * 创建服务代理
     */
    public <T> T createProxy(Class<T> serviceInterface) {
        return createProxy(serviceInterface, Collections.emptySet());
    }
    
    /**
     * 创建服务代理（带标签选择）
     */
    @SuppressWarnings("unchecked")
    public <T> T createProxy(Class<T> serviceInterface, Set<String> tags) {
        return (T) Proxy.newProxyInstance(
            serviceInterface.getClassLoader(),
            new Class[]{serviceInterface},
            new ServiceInvocationHandler(brokerClient, serviceInterface.getName(), tags)
        );
    }
    
    /**
     * 服务调用处理器
     */
    @AllArgsConstructor
    @FieldDefaults(level = AccessLevel.PRIVATE)
    private static class ServiceInvocationHandler implements InvocationHandler {
        final BrokerClient brokerClient;
        final String serviceInterface;
        final Set<String> serviceTags;
        
        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            // 处理Object类的方法
            if (method.getDeclaringClass() == Object.class) {
                return method.invoke(this, args);
            }

            args = args != null ? args : new Object[0];
            byte[] parameters = HessianSerializer.serialize(args);
            // 构建调用消息
            RpcInvocationMessage message = new RpcInvocationMessage()
                .setServiceInterface(serviceInterface)
                .setMethodName(method.getName())
                .setParameterTypes(getParameterTypeNames(method.getParameterTypes()))
                .setParameters(parameters)
                .setServiceTags(serviceTags);
            
            try {
                log.debug("Invoking remote service: {}.{}", serviceInterface, method.getName());
                byte[] result = brokerClient.invokeSync(message);
                if (result == null) return null;
                return HessianSerializer.deserialize(result);
            } catch (Exception e) {
                log.error("RPC invocation failed: {}.{}", serviceInterface, method.getName(), e);
                throw new InvokeException("RPC invocation failed: " + serviceInterface + "." + method.getName(), e);
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