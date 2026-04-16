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
import net.afyer.afybroker.client.preprocessor.BrokerInvocationContext;
import net.afyer.afybroker.client.preprocessor.BrokerPreprocessor;
import net.afyer.afybroker.client.preprocessor.PreprocessorException;
import net.afyer.afybroker.client.service.BrokerServiceProxyFactory;
import net.afyer.afybroker.client.service.BrokerServiceRegistry;
import net.afyer.afybroker.core.BrokerClientInfo;
import net.afyer.afybroker.core.message.AttributeMessage;
import net.afyer.afybroker.core.observability.Observability;
import net.afyer.afybroker.core.serializer.HessianSerializer;
import org.slf4j.Logger;

import java.util.Arrays;
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

    /**
     * 预处理函数列表
     */
    private List<BrokerPreprocessor> preprocessors;
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

    void setPreprocessors(List<BrokerPreprocessor> preprocessors) {
        this.preprocessors = preprocessors;
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

    public List<BrokerPreprocessor> getPreprocessors() {
        return preprocessors;
    }

    public Observability getObservability() {
        return observability;
    }

    public boolean hasTag(String tag) {
        return clientInfo.getTags().contains(tag);
    }

    public <T> T invokeSync(Object request) throws RemotingException, InterruptedException {
        return invokeSync(request, defaultTimeoutMillis);
    }

    @SuppressWarnings("unchecked")
    public <T> T invokeSync(Object request, int timeoutMillis) throws RemotingException, InterruptedException {
        executePreprocessors(request, "invokeSync", timeoutMillis);
        return (T) rpcClient.invokeSync(clientInfo.getAddress(), request, timeoutMillis);
    }

    public void oneway(Object request) throws RemotingException, InterruptedException {
        executePreprocessors(request, "oneway", 0);
        rpcClient.oneway(clientInfo.getAddress(), request);
    }

    public void invokeWithCallback(Object request, InvokeCallback invokeCallback) throws RemotingException, InterruptedException {
        invokeWithCallback(request, invokeCallback, defaultTimeoutMillis);
    }

    public void invokeWithCallback(Object request, InvokeCallback invokeCallback, int timeoutMillis) throws RemotingException, InterruptedException {
        executePreprocessors(request, "invokeWithCallback", timeoutMillis);
        rpcClient.invokeWithCallback(clientInfo.getAddress(), request, invokeCallback, timeoutMillis);
    }

    public RpcResponseFuture invokeWithFuture(Object request) throws RemotingException, InterruptedException {
        return invokeWithFuture(request, defaultTimeoutMillis);
    }

    public RpcResponseFuture invokeWithFuture(Object request, int timeoutMillis) throws RemotingException, InterruptedException {
        executePreprocessors(request, "invokeWithFuture", timeoutMillis);
        return rpcClient.invokeWithFuture(clientInfo.getAddress(), request, timeoutMillis);
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

    /**
     * 为直接调用方法执行预处理函数
     *
     * @param request    请求对象
     * @param methodName 方法名称（用于日志）
     */
    private void executePreprocessors(Object request, String methodName, int timeoutMillis) throws PreprocessorException {
        if (preprocessors != null && !preprocessors.isEmpty()) {
            // 创建通用的调用上下文
            BrokerInvocationContext context = new BrokerInvocationContext(
                    request.getClass().getName(),
                    methodName,
                    timeoutMillis,
                    Thread.currentThread()
            );

            for (BrokerPreprocessor preprocessor : preprocessors) {
                preprocessor.preprocess(context);
            }
        }
    }

    public Serializer getSerializer() {
        return SerializerManager.getSerializer(ConfigManager.serializer());
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
