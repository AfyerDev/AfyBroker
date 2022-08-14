package net.afyer.afybroker.client;

import com.alipay.remoting.ConnectionEventProcessor;
import com.alipay.remoting.ConnectionEventType;
import com.alipay.remoting.InvokeCallback;
import com.alipay.remoting.InvokeContext;
import com.alipay.remoting.config.BoltClientOption;
import com.alipay.remoting.exception.RemotingException;
import com.alipay.remoting.rpc.RpcClient;
import com.alipay.remoting.rpc.RpcResponseFuture;
import com.alipay.remoting.rpc.protocol.UserProcessor;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import net.afyer.afybroker.client.aware.BrokerClientAware;
import net.afyer.afybroker.core.BrokerGlobalConfig;
import net.afyer.afybroker.core.message.BrokerClientInfoMessage;

/**
 * @author Nipuru
 * @since 2022/7/30 19:15
 */
@Slf4j
@Getter
@Setter(AccessLevel.PACKAGE)
@FieldDefaults(level = AccessLevel.PRIVATE)
public class BrokerClient {

    /** 客户端信息 */
    BrokerClientInfoMessage clientInfo;

    final RpcClient rpcClient;

    /**
     * 消息发送超时时间
     */
    final int timeoutMillis = BrokerGlobalConfig.TIMEOUT_MILLIS;

    BrokerClient() {
        this.rpcClient = new RpcClient();
        rpcClient.option(BoltClientOption.CONN_RECONNECT_SWITCH, true);
        rpcClient.option(BoltClientOption.CONN_MONITOR_SWITCH, true);
    }

    @SuppressWarnings("unchecked")
    public <T> T invokeSync(Object request) {
        try {
            return (T) rpcClient.invokeSync(clientInfo.getAddress(), request, timeoutMillis);
        } catch (RemotingException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public void oneway(Object request) {
        try {
            rpcClient.oneway(clientInfo.getAddress(), request);
        } catch (RemotingException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public void invokeWithCallback(Object request, InvokeCallback invokeCallback) {
        try {
            rpcClient.invokeWithCallback(clientInfo.getAddress(), request, invokeCallback, timeoutMillis);
        } catch (RemotingException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public RpcResponseFuture invokeWithFuture(Object request, InvokeContext invokeContext) {
        try {
            return rpcClient.invokeWithFuture(clientInfo.getAddress(), request, invokeContext, timeoutMillis);
        } catch (RemotingException | InterruptedException e) {
            throw new RuntimeException(e);
        }
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

    public void ping() {
        String address = clientInfo.getAddress();

        try {
            rpcClient.getConnection(address, timeoutMillis);
        } catch (RemotingException | InterruptedException e) {
            log.error(e.getMessage());
        }
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
