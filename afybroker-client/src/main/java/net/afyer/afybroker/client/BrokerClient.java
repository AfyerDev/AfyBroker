package net.afyer.afybroker.client;

import com.alipay.remoting.ConnectionEventProcessor;
import com.alipay.remoting.ConnectionEventType;
import com.alipay.remoting.InvokeCallback;
import com.alipay.remoting.config.BoltClientOption;
import com.alipay.remoting.exception.RemotingException;
import com.alipay.remoting.rpc.RpcClient;
import com.alipay.remoting.rpc.RpcResponseFuture;
import com.alipay.remoting.rpc.protocol.UserProcessor;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import net.afyer.afybroker.client.aware.BrokerClientAware;
import net.afyer.afybroker.core.BrokerClientInfo;
import net.afyer.afybroker.core.BrokerGlobalConfig;

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

    final RpcClient rpcClient;

    /** 默认消息发送超时时间 */
    final int defaultTimeoutMillis = BrokerGlobalConfig.DEFAULT_TIMEOUT_MILLIS;

    BrokerClient() {
        this.rpcClient = new RpcClient();
        rpcClient.option(BoltClientOption.CONN_RECONNECT_SWITCH, true);
        rpcClient.option(BoltClientOption.CONN_MONITOR_SWITCH, true);
    }

    public boolean hasTag(String tag) {
        return clientInfo.getTags().contains(tag);
    }

    public <T> T invokeSync(Object request) throws RemotingException, InterruptedException {
        return invokeSync(request, defaultTimeoutMillis);
    }

    @SuppressWarnings("unchecked")
    public <T> T invokeSync(Object request, int timeoutMillis) throws RemotingException, InterruptedException {
        return (T) rpcClient.invokeSync(clientInfo.getAddress(), request, timeoutMillis);
    }

    public void oneway(Object request) throws RemotingException, InterruptedException {
        rpcClient.oneway(clientInfo.getAddress(), request);
    }

    public void invokeWithCallback(Object request, InvokeCallback invokeCallback) throws RemotingException, InterruptedException {
        invokeWithCallback(request, invokeCallback, defaultTimeoutMillis);
    }

    public void invokeWithCallback(Object request, InvokeCallback invokeCallback, int timeoutMillis) throws RemotingException, InterruptedException {
        rpcClient.invokeWithCallback(clientInfo.getAddress(), request, invokeCallback, timeoutMillis);
    }

    public RpcResponseFuture invokeWithFuture(Object request) throws RemotingException, InterruptedException {
        return invokeWithFuture(request, defaultTimeoutMillis);
    }

    public RpcResponseFuture invokeWithFuture(Object request, int timeoutMillis) throws RemotingException, InterruptedException {
        return rpcClient.invokeWithFuture(clientInfo.getAddress(), request, timeoutMillis);
    }

    public void registerUserProcessor(UserProcessor<?> processor){
        aware(processor);
        rpcClient.registerUserProcessor(processor);
    }
    public void addConnectionEventProcessor(ConnectionEventType type, ConnectionEventProcessor processor) {
        aware(processor);
        rpcClient.addConnectionEventProcessor(type, processor);
    }

    public void startup() {
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
        if (object instanceof BrokerClientAware brokerClientAware) {
            brokerClientAware.setBrokerClient(this);
        }
    }

    public static BrokerClientBuilder newBuilder() {
        return new BrokerClientBuilder();
    }
}
