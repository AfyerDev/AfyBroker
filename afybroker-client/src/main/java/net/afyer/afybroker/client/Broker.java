package net.afyer.afybroker.client;

import com.alipay.remoting.ConnectionEventProcessor;
import com.alipay.remoting.ConnectionEventType;
import com.alipay.remoting.InvokeCallback;
import com.alipay.remoting.exception.RemotingException;
import com.alipay.remoting.rpc.RpcClient;
import com.alipay.remoting.rpc.RpcResponseFuture;
import com.alipay.remoting.rpc.protocol.UserProcessor;
import lombok.Getter;
import net.afyer.afybroker.core.BrokerClientInfo;

/**
 * @author Nipuru
 * @since 2022/9/19 23:30
 */
public final class Broker {

    /**
     * broker 客户端
     */
    @Getter
    private static BrokerClient client;

    private Broker() {}

    /** 设置 broker 客户端 */
    public static void setClient(BrokerClient client) {
        if (Broker.client != null) {
            throw new UnsupportedOperationException("Cannot redefine singleton instance");
        }
        Broker.client = client;
    }

    /**
     * 获取客户端信息
     */
    public static BrokerClientInfo getClientInfo() {
        return client.getClientInfo();
    }

    /**
     * 获取 rpc 客户端
     */
    public static RpcClient getRpcClient() {
        return client.getRpcClient();
    }

    /**
     * 获取通信超时时间（ms）
     */
    public static int getDefaultTimeoutMillis() {
        return client.getDefaultTimeoutMillis();
    }

    /**
     * 判断客户端是否含有指定标签
     */
    public static boolean hasTag(String tag) {
        return client.hasTag(tag);
    }

    /**
     * 添加额外标签（必须在 onLoad 阶段）
     */
    public void addExtraTag(String tag) {
        client.addExtraTag(tag);
    }

    /**
     * 发送消息 sync
     */
    public static <T> T invokeSync(Object request) throws RemotingException, InterruptedException {
        return client.invokeSync(request);
    }

    /**
     * 发送消息 sync
     */
    public static <T> T invokeSync(Object request, int timeoutMillis) throws RemotingException, InterruptedException {
        return client.invokeSync(request, timeoutMillis);
    }

    /**
     * 发送消息 oneway
     */
    public static void oneway(Object request) throws RemotingException, InterruptedException {
        client.oneway(request);
    }

    /**
     * 发送消息 callback
     */
    public static void invokeWithCallback(Object request, InvokeCallback invokeCallback) throws RemotingException, InterruptedException {
        client.invokeWithCallback(request, invokeCallback);
    }

    /**
     * 发送消息 callback
     */
    public static void invokeWithCallback(Object request, InvokeCallback invokeCallback, int timeoutMillis) throws RemotingException, InterruptedException {
        client.invokeWithCallback(request, invokeCallback, timeoutMillis);
    }

    /**
     * 发送消息 future
     */
    public static RpcResponseFuture invokeWithFuture(Object request) throws RemotingException, InterruptedException {
        return client.invokeWithFuture(request);
    }

    /**
     * 发送消息 future
     */
    public static RpcResponseFuture invokeWithFuture(Object request, int timeoutMillis) throws RemotingException, InterruptedException {
        return client.invokeWithFuture(request, timeoutMillis);
    }


    /**
     * 注册用户消息处理器
     */
    public static void registerUserProcessor(UserProcessor<?> processor) {
        client.registerUserProcessor(processor);
    }

    /**
     * 注册连接处理器
     */
    public static void addConnectionEventProcessor(ConnectionEventType type, ConnectionEventProcessor processor) {
        client.addConnectionEventProcessor(type, processor);
    }

    /**
     * 注入 brokerClient
     *
     * @see net.afyer.afybroker.client.aware.BrokerClientAware
     */
    public static void aware(Object object) {
        client.aware(object);
    }
}
