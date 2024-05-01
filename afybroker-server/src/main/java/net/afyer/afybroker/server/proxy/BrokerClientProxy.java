package net.afyer.afybroker.server.proxy;

import com.alipay.remoting.InvokeCallback;
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

    /** 默认消息发送超时时间 */
    final int defaultTimeoutMillis = BrokerGlobalConfig.DEFAULT_TIMEOUT_MILLIS;

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

    public boolean hasAnyTags(String... tags) {
        for (String tag : tags) {
            if (this.tags.contains(tag)) {
                return true;
            }
        }
        return false;
    }

    public boolean hasAnyTags(Iterable<String> tags) {
        for (String tag : tags) {
            if (this.tags.contains(tag)) {
                return true;
            }
        }
        return false;
    }

    public boolean hasAllTags(String... tags) {
        for (String tag : tags) {
            if (!this.tags.contains(tag)) {
                return false;
            }
        }
        return true;
    }

    public boolean hasAllTags(Iterable<String> tags) {
        for (String tag : tags) {
            if (!this.tags.contains(tag)) {
                return false;
            }
        }
        return true;
    }

    public <T> T invokeSync(Object request) throws RemotingException, InterruptedException {
        return invokeSync(request, defaultTimeoutMillis);
    }

    @SuppressWarnings("unchecked")
    public <T> T invokeSync(Object request, int timeoutMillis) throws RemotingException, InterruptedException {
        return (T) rpcServer.invokeSync(address, request, timeoutMillis);
    }

    public void oneway(Object request) throws RemotingException, InterruptedException {
        rpcServer.oneway(address, request);
    }

    public void invokeWithCallback(Object request, InvokeCallback invokeCallback) throws RemotingException, InterruptedException {
        invokeWithCallback(request, invokeCallback, defaultTimeoutMillis);
    }

    public void invokeWithCallback(Object request, InvokeCallback invokeCallback, int timeoutMillis) throws RemotingException, InterruptedException {
        rpcServer.invokeWithCallback(address, request, invokeCallback, timeoutMillis);
    }

    public RpcResponseFuture invokeWithFuture(Object request) throws RemotingException, InterruptedException {
        return invokeWithFuture(request, defaultTimeoutMillis);
    }

    public RpcResponseFuture invokeWithFuture(Object request, int timeoutMillis) throws RemotingException, InterruptedException {
        return rpcServer.invokeWithFuture(address, request, timeoutMillis);
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
