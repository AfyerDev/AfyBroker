package net.afyer.afybroker.server.proxy;

import com.alipay.remoting.InvokeCallback;
import com.alipay.remoting.InvokeContext;
import com.alipay.remoting.exception.RemotingException;
import com.alipay.remoting.rpc.RpcResponseFuture;
import com.alipay.remoting.rpc.RpcServer;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.ToString;
import lombok.experimental.FieldDefaults;
import net.afyer.afybroker.core.message.BrokerClientInfoMessage;
import net.afyer.afybroker.core.BrokerClientType;
import net.afyer.afybroker.core.BrokerGlobalConfig;

/**
 * 客户端代理
 *
 * @author Nipuru
 * @since 2022/7/30 16:15
 */
@Getter
@ToString
@FieldDefaults(level = AccessLevel.PRIVATE)
public class BrokerClientProxy {

    /** 客户端名称(唯一标识) */
    final String name;
    /** 客户端标签 */
    final String tag;
    /** 客户端类型 */
    final BrokerClientType type;
    /** 客户端地址 */
    final String address;

    final RpcServer rpcServer;

    /** 消息发送超时时间 */
    final int timeoutMillis = BrokerGlobalConfig.timeoutMillis;

    public BrokerClientProxy(BrokerClientInfoMessage clientInfo, RpcServer rpcServer) {
        this.name = clientInfo.getName();
        this.tag = clientInfo.getTag();
        this.type = clientInfo.getType();
        this.address = clientInfo.getAddress();
        this.rpcServer = rpcServer;
    }

    @SuppressWarnings("unchecked")
    public <T> T invokeSync(Object request) throws RemotingException, InterruptedException {
        return (T) rpcServer.invokeSync(address, request, timeoutMillis);
    }

    public void oneway(Object request) throws RemotingException, InterruptedException {
        rpcServer.oneway(address, request);
    }

    public void invokeWithCallback(Object request, InvokeCallback invokeCallback) throws RemotingException, InterruptedException {
        rpcServer.invokeWithCallback(address, request, invokeCallback, timeoutMillis);
    }

    public RpcResponseFuture invokeWithFuture(Object request, InvokeContext invokeContext) throws RemotingException, InterruptedException {
        return rpcServer.invokeWithFuture(address, request, invokeContext, timeoutMillis);
    }


}
