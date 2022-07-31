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
import net.afyer.afybroker.core.BrokerClientType;
import net.afyer.afybroker.core.BrokerGlobalConfig;

/**
 * @author Nipuru
 * @since 2022/7/30 19:15
 */
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class BrokerClient {

    /** 客户端名称(唯一标识) */
    final String name;
    /** 客户端标签 */
    final String tag;
    /** 客户端类型 */
    final BrokerClientType type;
    /** ip:port */
    final String address;

    final RpcClient rpcClient;

    /** 消息发送超时时间 */
    final int timeoutMillis = BrokerGlobalConfig.timeoutMillis;

    public BrokerClient(String name, String tag, BrokerClientType type, String address) {
        this.name = name;
        this.tag = tag;
        this.type = type;
        this.address = address;
        this.rpcClient = new RpcClient();
        rpcClient.option(BoltClientOption.CONN_RECONNECT_SWITCH, true);
        rpcClient.option(BoltClientOption.CONN_MONITOR_SWITCH, true);
    }

    @SuppressWarnings("unchecked")
    public <T> T invokeSync(Object request) throws RemotingException, InterruptedException {
        return (T) rpcClient.invokeSync(address, request, timeoutMillis);
    }

    public void oneway(Object request) throws RemotingException, InterruptedException {
        rpcClient.oneway(address, request);
    }

    public void invokeWithCallback(Object request, InvokeCallback invokeCallback) throws RemotingException, InterruptedException {
        rpcClient.invokeWithCallback(address, request, invokeCallback, timeoutMillis);
    }

    public RpcResponseFuture invokeWithFuture(Object request, InvokeContext invokeContext) throws RemotingException, InterruptedException {
        return rpcClient.invokeWithFuture(address, request, invokeContext, timeoutMillis);
    }
}
