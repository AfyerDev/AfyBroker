package net.afyer.afybroker.client;

import com.alipay.remoting.InvokeCallback;
import com.alipay.remoting.LifeCycleException;
import com.alipay.remoting.exception.RemotingException;
import com.alipay.remoting.rpc.RpcClient;
import com.alipay.remoting.rpc.RpcResponseFuture;
import com.alipay.remoting.serialization.SerializerManager;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import net.afyer.afybroker.client.aware.BrokerClientAware;
import net.afyer.afybroker.client.preprocessor.BrokerInvocationContext;
import net.afyer.afybroker.client.preprocessor.BrokerPreprocessor;
import net.afyer.afybroker.client.preprocessor.PreprocessorException;
import net.afyer.afybroker.client.service.BrokerServiceProxyFactory;
import net.afyer.afybroker.client.service.BrokerServiceRegistry;
import net.afyer.afybroker.core.BrokerClientInfo;
import net.afyer.afybroker.core.util.HessianSerializer;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

/**
 * @author Nipuru
 * @since 2022/7/30 19:15
 */
@Getter
@Setter(AccessLevel.PACKAGE)
@FieldDefaults(level = AccessLevel.PRIVATE)
public class BrokerClient {

    /** 客户端信息 */
    BrokerClientInfo clientInfo;

    /** rpc 客户端 */
    @Getter(AccessLevel.NONE)
    RpcClient rpcClient;

    /** 消息发送超时时间 */
    int defaultTimeoutMillis;

    /** 服务注册表 */
    BrokerServiceRegistry serviceRegistry;
    
    /** 服务代理工厂 */
    final BrokerServiceProxyFactory serviceProxyFactory;

    /** 预处理函数列表 */
    List<BrokerPreprocessor> preprocessors;

    BrokerClient() {
        this.serviceProxyFactory = new BrokerServiceProxyFactory(this);
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
     * @param request 请求对象
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

    public static BrokerClientBuilder newBuilder() {
        return new BrokerClientBuilder();
    }

    static {
        SerializerManager.addSerializer(SerializerManager.Hessian2, new HessianSerializer(BrokerClient.class.getClassLoader()));
    }
}
