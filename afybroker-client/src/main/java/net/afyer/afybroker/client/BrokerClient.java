package net.afyer.afybroker.client;

import com.alipay.remoting.InvokeCallback;
import com.alipay.remoting.LifeCycleException;
import com.alipay.remoting.config.ConfigManager;
import com.alipay.remoting.exception.RemotingException;
import com.alipay.remoting.rpc.RpcClient;
import com.alipay.remoting.rpc.RpcResponseFuture;
import com.alipay.remoting.serialization.Serializer;
import com.alipay.remoting.serialization.SerializerManager;
import net.afyer.afybroker.client.aware.BrokerClientAware;
import net.afyer.afybroker.client.service.BrokerServiceProxyFactory;
import net.afyer.afybroker.client.service.BrokerServiceRegistry;
import net.afyer.afybroker.core.BrokerClientInfo;
import net.afyer.afybroker.core.interceptor.*;
import net.afyer.afybroker.core.message.AttributeMessage;
import net.afyer.afybroker.core.observability.Observability;
import net.afyer.afybroker.core.serializer.HessianSerializer;
import net.afyer.afybroker.core.util.ThrowableUtils;
import org.slf4j.Logger;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.UUID;

/**
 * @author Nipuru
 * @since 2022/7/30 19:15
 */
public class BrokerClient {
    /**
     * 客户端信息
     */
    private BrokerClientInfo clientInfo;

    /**
     * rpc 客户端
     */
    private RpcClient rpcClient;

    /**
     * 消息发送超时时间
     */
    private int defaultTimeoutMillis;

    /**
     * 服务注册表
     */
    private BrokerServiceRegistry serviceRegistry;

    /**
     * 服务代理工厂
     */
    private final BrokerServiceProxyFactory serviceProxyFactory;

    private List<Interceptor> interceptors = Collections.emptyList();
    /**
     * 指标收集器
     */
    private Observability observability = Observability.NOOP;

    BrokerClient() {
        this.serviceProxyFactory = new BrokerServiceProxyFactory(this);
    }

    void setClientInfo(BrokerClientInfo clientInfo) {
        this.clientInfo = clientInfo;
    }

    void setRpcClient(RpcClient rpcClient) {
        this.rpcClient = rpcClient;
    }

    void setDefaultTimeoutMillis(int defaultTimeoutMillis) {
        this.defaultTimeoutMillis = defaultTimeoutMillis;
    }

    void setServiceRegistry(BrokerServiceRegistry serviceRegistry) {
        this.serviceRegistry = serviceRegistry;
    }

    void setInterceptors(List<Interceptor> interceptors) {
        this.interceptors = interceptors == null ? Collections.emptyList() : interceptors;
    }

    void setObservability(Observability observability) {
        this.observability = observability;
    }

    public BrokerClientInfo getClientInfo() {
        return clientInfo;
    }

    public int getDefaultTimeoutMillis() {
        return defaultTimeoutMillis;
    }

    public BrokerServiceRegistry getServiceRegistry() {
        return serviceRegistry;
    }

    public BrokerServiceProxyFactory getServiceProxyFactory() {
        return serviceProxyFactory;
    }

    public Observability getObservability() {
        return observability;
    }

    public String getName() {
        return clientInfo.getName();
    }

    public String getType() {
        return clientInfo.getType();
    }

    public boolean hasMetadata(String key) {
        return clientInfo.hasMetadata(key);
    }

    public String getMetadata(String key) {
        return clientInfo.getMetadata(key);
    }

    public boolean hasTag(String tag) {
        return clientInfo.hasTag(tag);
    }

    public <T> T invokeSync(Object request) throws RemotingException, InterruptedException {
        return invokeSync(request, defaultTimeoutMillis);
    }

    @SuppressWarnings("unchecked")
    public <T> T invokeSync(Object request, int timeoutMillis) throws RemotingException, InterruptedException {
        InvocationContext context = newInvocationContext(request, InvocationType.SYNC, timeoutMillis);
        return (T) invokeWithInterceptors(context, new Invoker() {
            @Override
            public Object invoke(InvocationContext invocationContext) throws Throwable {
                return rpcClient.invokeSync(invocationContext.getAddress(),
                        invocationContext.getRequest(), invocationContext.getTimeoutMillis());
            }
        });
    }

    public void oneway(Object request) throws RemotingException, InterruptedException {
        InvocationContext context = newInvocationContext(request, InvocationType.ONEWAY, 0);
        invokeWithInterceptors(context, new Invoker() {
            @Override
            public Object invoke(InvocationContext invocationContext) throws Throwable {
                rpcClient.oneway(invocationContext.getAddress(), invocationContext.getRequest());
                return null;
            }
        });
    }

    public void invokeWithCallback(Object request, InvokeCallback invokeCallback) throws RemotingException, InterruptedException {
        invokeWithCallback(request, invokeCallback, defaultTimeoutMillis);
    }

    public void invokeWithCallback(Object request, InvokeCallback invokeCallback, int timeoutMillis) throws RemotingException, InterruptedException {
        InvocationContext context = newInvocationContext(request, InvocationType.CALLBACK, timeoutMillis)
                .setCallback(invokeCallback);
        invokeWithInterceptors(context, new Invoker() {
            @Override
            public Object invoke(InvocationContext invocationContext) throws Throwable {
                rpcClient.invokeWithCallback(invocationContext.getAddress(), invocationContext.getRequest(),
                        (InvokeCallback) invocationContext.getCallback(), invocationContext.getTimeoutMillis());
                return null;
            }
        });
    }

    public RpcResponseFuture invokeWithFuture(Object request) throws RemotingException, InterruptedException {
        return invokeWithFuture(request, defaultTimeoutMillis);
    }

    public RpcResponseFuture invokeWithFuture(Object request, int timeoutMillis) throws RemotingException, InterruptedException {
        InvocationContext context = newInvocationContext(request, InvocationType.FUTURE, timeoutMillis);
        return (RpcResponseFuture) invokeWithInterceptors(context, new Invoker() {
            @Override
            public Object invoke(InvocationContext invocationContext) throws RemotingException, InterruptedException {
                return rpcClient.invokeWithFuture(invocationContext.getAddress(), invocationContext.getRequest(), invocationContext.getTimeoutMillis());
            }
        });
    }

    public void startup() throws LifeCycleException {
        rpcClient.startup();
    }

    public void shutdown() {
        rpcClient.shutdown();
    }

    public void ping() throws RemotingException, InterruptedException {
        String address = clientInfo.getAddress();

        rpcClient.getConnection(address, defaultTimeoutMillis);
    }

    public void aware(Object object) {
        if (object instanceof BrokerClientAware) {
            ((BrokerClientAware) object).setBrokerClient(this);
        }
    }

    public <T> T getService(Class<T> serviceInterface) {
        return serviceProxyFactory.createProxy(serviceInterface);
    }

    public <T> T getService(Class<T> serviceInterface, String... tags) {
        return serviceProxyFactory.createProxy(serviceInterface, new HashSet<>(Arrays.asList(tags)));
    }

    public Serializer getSerializer() {
        return SerializerManager.getSerializer(ConfigManager.serializer());
    }

    private InvocationContext newInvocationContext(Object request, InvocationType mode, int timeoutMillis) {
        return new InvocationContext(request, mode, clientInfo.getAddress(), timeoutMillis);
    }

    private Object invokeWithInterceptors(InvocationContext context, Invoker invoker) {
        try {
            return InterceptorChain.invoke(interceptors, context, invoker);
        } catch (Throwable throwable) {
            return ThrowableUtils.throwUnchecked(throwable);
        }
    }

    /**
     * 设置服务器全局属性
     */
    public <T> void setServerAttribute(String key, T value) throws RemotingException, InterruptedException {
        Serializer serializer = getSerializer();
        AttributeMessage msg = new AttributeMessage()
                .setAction(AttributeMessage.ACTION_SET)
                .setScope(AttributeMessage.SCOPE_SERVER)
                .setKey(key)
                .setValue(serializer.serialize(value));
        invokeSync(msg);
    }

    /**
     * 获取服务器全局属性
     */
    public <T> T getServerAttribute(String key) throws RemotingException, InterruptedException {
        AttributeMessage msg = new AttributeMessage()
                .setAction(AttributeMessage.ACTION_GET)
                .setScope(AttributeMessage.SCOPE_SERVER)
                .setKey(key);
        return getSerializer().deserialize(invokeSync(msg), Object.class.getName());
    }

    /**
     * 移除服务器全局属性
     */
    public void removeServerAttribute(String key) throws RemotingException, InterruptedException {
        AttributeMessage msg = new AttributeMessage()
                .setAction(AttributeMessage.ACTION_REMOVE)
                .setScope(AttributeMessage.SCOPE_SERVER)
                .setKey(key);
        invokeSync(msg);
    }

    /**
     * 判断是否存在服务器全局属性
     */
    public boolean hasServerAttribute(String key) throws RemotingException, InterruptedException {
        AttributeMessage msg = new AttributeMessage()
                .setAction(AttributeMessage.ACTION_HAS)
                .setScope(AttributeMessage.SCOPE_SERVER)
                .setKey(key);
        return Boolean.TRUE.equals(invokeSync(msg));
    }

    /**
     * 设置玩家属性
     */
    public <T> void setPlayerAttribute(UUID uniqueId, String key, T value) throws RemotingException, InterruptedException {
        Serializer serializer = getSerializer();
        AttributeMessage msg = new AttributeMessage()
                .setAction(AttributeMessage.ACTION_SET)
                .setScope(AttributeMessage.SCOPE_PLAYER)
                .setUniqueId(uniqueId)
                .setKey(key)
                .setValue(serializer.serialize(value));
        invokeSync(msg);
    }

    /**
     * 获取玩家属性
     */
    public <T> T getPlayerAttribute(UUID uniqueId, String key) throws RemotingException, InterruptedException {
        AttributeMessage msg = new AttributeMessage()
                .setAction(AttributeMessage.ACTION_GET)
                .setScope(AttributeMessage.SCOPE_PLAYER)
                .setUniqueId(uniqueId)
                .setKey(key);
        return getSerializer().deserialize(invokeSync(msg), Object.class.getName());
    }

    /**
     * 移除玩家属性
     */
    public void removePlayerAttribute(UUID uniqueId, String key) throws RemotingException, InterruptedException {
        AttributeMessage msg = new AttributeMessage()
                .setAction(AttributeMessage.ACTION_REMOVE)
                .setScope(AttributeMessage.SCOPE_PLAYER)
                .setUniqueId(uniqueId)
                .setKey(key);
        invokeSync(msg);
    }

    /**
     * 判断是否存在玩家属性
     */
    public boolean hasPlayerAttribute(UUID uniqueId, String key) throws RemotingException, InterruptedException {
        AttributeMessage msg = new AttributeMessage()
                .setAction(AttributeMessage.ACTION_HAS)
                .setScope(AttributeMessage.SCOPE_PLAYER)
                .setUniqueId(uniqueId)
                .setKey(key);
        return Boolean.TRUE.equals(invokeSync(msg));
    }

    public void printInformation(Logger logger) {
        logger.info("========== BrokerClient Information ==========");
        logger.info("Name: {}", clientInfo.getName());
        logger.info("Type: {}", clientInfo.getType());
        logger.info("Tags: {}", clientInfo.getTags());
        logger.info("Metadata: {}", clientInfo.getMetadata());
        logger.info("Address: {}", clientInfo.getAddress());
        logger.info("Default Timeout: {} ms", defaultTimeoutMillis);
        logger.info("================================================");
    }

    public static BrokerClientBuilder newBuilder() {
        return new BrokerClientBuilder();
    }

    static {
        // 添加默认序列化器
        ClassLoader classLoader = BrokerClient.class.getClassLoader();
        SerializerManager.addSerializer(SerializerManager.Hessian2, new HessianSerializer(classLoader));
    }
}
