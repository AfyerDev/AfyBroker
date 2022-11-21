package net.afyer.afybroker.server.proxy;

import com.alipay.remoting.InvokeCallback;
import com.alipay.remoting.InvokeContext;
import com.alipay.remoting.exception.RemotingException;
import com.alipay.remoting.rpc.RpcResponseFuture;
import com.alipay.remoting.rpc.RpcServer;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import net.afyer.afybroker.core.BrokerClientType;
import net.afyer.afybroker.core.BrokerGlobalConfig;
import net.afyer.afybroker.core.message.BrokerClientInfoMessage;

import java.util.Collections;
import java.util.Set;

/**
 * 客户端代理
 *
 * @author Nipuru
 * @since 2022/7/30 16:15
 */
@Getter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class BrokerClientProxy {

    /** 客户端名称(唯一标识) */
    final String name;
    /** 客户端标签 */
    final Set<String> tags;
    /** 客户端类型 */
    final BrokerClientType type;
    /** 客户端地址 */
    final String address;

    final RpcServer rpcServer;

    /** 消息发送超时时间 */
    final int timeoutMillis = BrokerGlobalConfig.TIMEOUT_MILLIS;

    public BrokerClientProxy(BrokerClientInfoMessage clientInfo, RpcServer rpcServer) {
        this.name = clientInfo.getName();
        this.tags = Collections.unmodifiableSet(clientInfo.getTags());
        this.type = clientInfo.getType();
        this.address = clientInfo.getAddress();
        this.rpcServer = rpcServer;
    }

    public boolean hasTag(String tag) {
        return tags.contains(tag);
    }

    @SuppressWarnings("unchecked")
    public <T> T invokeSync(Object request) {
        try {
            return (T) rpcServer.invokeSync(address, request, timeoutMillis);
        } catch (RemotingException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public void oneway(Object request) {
        try {
            rpcServer.oneway(address, request);
        } catch (RemotingException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public void invokeWithCallback(Object request, InvokeCallback invokeCallback) {
        try {
            rpcServer.invokeWithCallback(address, request, invokeCallback, timeoutMillis);
        } catch (RemotingException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public RpcResponseFuture invokeWithFuture(Object request, InvokeContext invokeContext) {
        try {
            return rpcServer.invokeWithFuture(address, request, invokeContext, timeoutMillis);
        } catch (RemotingException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public String toString() {
        return "BrokerClientProxy{" +
                "name='" + name + '\'' +
                ", tags='" + tags + '\'' +
                ", type=" + type +
                ", address='" + address + '\'' +
                '}';
    }
}
