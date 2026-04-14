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
import net.afyer.afybroker.core.observability.LifecycleState;
import net.afyer.afybroker.core.observability.Observability;
import net.afyer.afybroker.core.observability.ObservabilitySupport;
import net.afyer.afybroker.core.observability.PlayerEventType;
import net.afyer.afybroker.core.observability.PlayerObservation;
import net.afyer.afybroker.core.observability.RpcObservation;
import net.afyer.afybroker.core.observability.RpcPhase;
import net.afyer.afybroker.core.serializer.HessianSerializer;
import org.slf4j.Logger;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.Executor;

public class BrokerClient {

    private BrokerClientInfo clientInfo;
    private RpcClient rpcClient;
    private int defaultTimeoutMillis;
    private BrokerServiceRegistry serviceRegistry;
    private final BrokerServiceProxyFactory serviceProxyFactory;
    private List<BrokerPreprocessor> preprocessors;
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
        long startNanos = System.nanoTime();
        try {
            T result = (T) rpcClient.invokeSync(clientInfo.getAddress(), request, timeoutMillis);
            recordRpc(RpcPhase.OUTBOUND, request, startNanos, null);
            return result;
        } catch (RemotingException | InterruptedException | RuntimeException e) {
            recordRpc(RpcPhase.OUTBOUND, request, startNanos, e);
            throw e;
        }
    }

    public void oneway(Object request) throws RemotingException, InterruptedException {
        executePreprocessors(request, "oneway", 0);
        long startNanos = System.nanoTime();
        try {
            rpcClient.oneway(clientInfo.getAddress(), request);
            recordRpc(RpcPhase.OUTBOUND, request, startNanos, null);
        } catch (RemotingException | InterruptedException | RuntimeException e) {
            recordRpc(RpcPhase.OUTBOUND, request, startNanos, e);
            throw e;
        }
    }

    public void invokeWithCallback(Object request, InvokeCallback invokeCallback) throws RemotingException, InterruptedException {
        invokeWithCallback(request, invokeCallback, defaultTimeoutMillis);
    }

    public void invokeWithCallback(Object request, InvokeCallback invokeCallback, int timeoutMillis) throws RemotingException, InterruptedException {
        executePreprocessors(request, "invokeWithCallback", timeoutMillis);
        long startNanos = System.nanoTime();
        rpcClient.invokeWithCallback(clientInfo.getAddress(), request,
                wrapCallback(request, invokeCallback, startNanos), timeoutMillis);
    }

    public RpcResponseFuture invokeWithFuture(Object request) throws RemotingException, InterruptedException {
        return invokeWithFuture(request, defaultTimeoutMillis);
    }

    public RpcResponseFuture invokeWithFuture(Object request, int timeoutMillis) throws RemotingException, InterruptedException {
        executePreprocessors(request, "invokeWithFuture", timeoutMillis);
        long startNanos = System.nanoTime();
        try {
            RpcResponseFuture future = rpcClient.invokeWithFuture(clientInfo.getAddress(), request, timeoutMillis);
            recordRpc(RpcPhase.OUTBOUND_FUTURE, request, startNanos, null);
            return future;
        } catch (RemotingException | InterruptedException | RuntimeException e) {
            recordRpc(RpcPhase.OUTBOUND_FUTURE, request, startNanos, e);
            throw e;
        }
    }

    public void startup() throws LifeCycleException {
        observability.onLifecycle(LifecycleState.STARTING);
        try {
            rpcClient.startup();
            observability.onLifecycle(LifecycleState.STARTED);
        } catch (LifeCycleException e) {
            observability.onLifecycle(LifecycleState.START_FAILED);
            throw e;
        }
    }

    public void shutdown() {
        observability.onLifecycle(LifecycleState.STOPPING);
        try {
            rpcClient.shutdown();
            observability.onLifecycle(LifecycleState.STOPPED);
        } finally {
            observability.close();
        }
    }

    public void ping() throws RemotingException, InterruptedException {
        rpcClient.getConnection(clientInfo.getAddress(), defaultTimeoutMillis);
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

    private void executePreprocessors(Object request, String methodName, int timeoutMillis) throws PreprocessorException {
        if (preprocessors != null && !preprocessors.isEmpty()) {
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

    public <T> void setServerAttribute(String key, T value) throws RemotingException, InterruptedException {
        Serializer serializer = getSerializer();
        AttributeMessage msg = new AttributeMessage()
                .setAction(AttributeMessage.ACTION_SET)
                .setScope(AttributeMessage.SCOPE_SERVER)
                .setKey(key)
                .setValue(serializer.serialize(value));
        invokeSync(msg);
    }

    public <T> T getServerAttribute(String key) throws RemotingException, InterruptedException {
        AttributeMessage msg = new AttributeMessage()
                .setAction(AttributeMessage.ACTION_GET)
                .setScope(AttributeMessage.SCOPE_SERVER)
                .setKey(key);
        return getSerializer().deserialize(invokeSync(msg), Object.class.getName());
    }

    public void removeServerAttribute(String key) throws RemotingException, InterruptedException {
        AttributeMessage msg = new AttributeMessage()
                .setAction(AttributeMessage.ACTION_REMOVE)
                .setScope(AttributeMessage.SCOPE_SERVER)
                .setKey(key);
        invokeSync(msg);
    }

    public boolean hasServerAttribute(String key) throws RemotingException, InterruptedException {
        AttributeMessage msg = new AttributeMessage()
                .setAction(AttributeMessage.ACTION_HAS)
                .setScope(AttributeMessage.SCOPE_SERVER)
                .setKey(key);
        return Boolean.TRUE.equals(invokeSync(msg));
    }

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

    public <T> T getPlayerAttribute(UUID uniqueId, String key) throws RemotingException, InterruptedException {
        AttributeMessage msg = new AttributeMessage()
                .setAction(AttributeMessage.ACTION_GET)
                .setScope(AttributeMessage.SCOPE_PLAYER)
                .setUniqueId(uniqueId)
                .setKey(key);
        return getSerializer().deserialize(invokeSync(msg), Object.class.getName());
    }

    public void removePlayerAttribute(UUID uniqueId, String key) throws RemotingException, InterruptedException {
        AttributeMessage msg = new AttributeMessage()
                .setAction(AttributeMessage.ACTION_REMOVE)
                .setScope(AttributeMessage.SCOPE_PLAYER)
                .setUniqueId(uniqueId)
                .setKey(key);
        invokeSync(msg);
    }

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

    public void recordPlayerEvent(PlayerEventType eventType, int onlinePlayers) {
        observability.onPlayer(new PlayerObservation(eventType, onlinePlayers));
    }

    public void recordOnlinePlayers(int onlinePlayers) {
        observability.onPlayer(new PlayerObservation(null, onlinePlayers));
    }

    public static BrokerClientBuilder newBuilder() {
        return new BrokerClientBuilder();
    }

    private void recordRpc(RpcPhase phase, Object request, long startNanos, Throwable error) {
        observability.onRpc(new RpcObservation(
                phase,
                ObservabilitySupport.requestType(request),
                ObservabilitySupport.serviceInterface(request),
                ObservabilitySupport.methodName(request),
                error == null,
                System.nanoTime() - startNanos
        ));
    }

    private InvokeCallback wrapCallback(Object request, InvokeCallback invokeCallback, long startNanos) {
        return new InvokeCallback() {
            @Override
            public void onResponse(Object result) {
                recordRpc(RpcPhase.OUTBOUND, request, startNanos, null);
                invokeCallback.onResponse(result);
            }

            @Override
            public void onException(Throwable e) {
                recordRpc(RpcPhase.OUTBOUND, request, startNanos, e);
                invokeCallback.onException(e);
            }

            @Override
            public Executor getExecutor() {
                return invokeCallback.getExecutor();
            }
        };
    }

    static {
        ClassLoader classLoader = BrokerClient.class.getClassLoader();
        SerializerManager.addSerializer(SerializerManager.Hessian2, new HessianSerializer(classLoader));
    }
}
