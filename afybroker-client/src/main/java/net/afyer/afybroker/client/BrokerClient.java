package net.afyer.afybroker.client;

import com.alipay.remoting.InvokeCallback;
import com.alipay.remoting.InvokeContext;
import com.alipay.remoting.config.BoltClientOption;
import com.alipay.remoting.exception.RemotingException;
import com.alipay.remoting.rpc.RpcClient;
import com.alipay.remoting.rpc.RpcResponseFuture;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import net.afyer.afybroker.core.BrokerClientInfoMessage;
import net.afyer.afybroker.core.BrokerGlobalConfig;

import java.util.concurrent.ExecutorService;

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

    /** 事务线程池 */
    ExecutorService bizThread;

    final RpcClient rpcClient;

    /** 消息发送超时时间 */
    final int timeoutMillis = BrokerGlobalConfig.timeoutMillis;

    BrokerClient() {
        this.rpcClient = new RpcClient();
        rpcClient.option(BoltClientOption.CONN_RECONNECT_SWITCH, true);
        rpcClient.option(BoltClientOption.CONN_MONITOR_SWITCH, true);
    }

    @SuppressWarnings("unchecked")
    public <T> T invokeSync(Object request) throws RemotingException, InterruptedException {
        return (T) rpcClient.invokeSync(clientInfo.getAddress(), request, timeoutMillis);
    }

    public void oneway(Object request) throws RemotingException, InterruptedException {
        rpcClient.oneway(clientInfo.getAddress(), request);
    }

    public void invokeWithCallback(Object request, InvokeCallback invokeCallback) throws RemotingException, InterruptedException {
        rpcClient.invokeWithCallback(clientInfo.getAddress(), request, invokeCallback, timeoutMillis);
    }

    public RpcResponseFuture invokeWithFuture(Object request, InvokeContext invokeContext) throws RemotingException, InterruptedException {
        return rpcClient.invokeWithFuture(clientInfo.getAddress(), request, invokeContext, timeoutMillis);
    }

    public void startup() {
        rpcClient.startup();
    }

    public void shutdown() {
        rpcClient.shutdown();
    }

    public void ping() throws RemotingException, InterruptedException {
        String address = clientInfo.getAddress();

        rpcClient.getConnection(address, timeoutMillis);
    }

    public static BrokerClientBuilder newBuilder() {
        return new BrokerClientBuilder();
    }
}
