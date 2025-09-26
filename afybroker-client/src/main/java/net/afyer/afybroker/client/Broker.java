package net.afyer.afybroker.client;

import com.alipay.remoting.InvokeCallback;
import com.alipay.remoting.exception.RemotingException;
import com.alipay.remoting.rpc.RpcResponseFuture;
import net.afyer.afybroker.core.BrokerClientInfo;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;

/**
 * brokerClient 全局单例
 *
 * @author Nipuru
 * @since 2022/9/19 23:30
 */
public final class Broker {

    /** broker 客户端 */
    private static BrokerClient client;

    public static BrokerClient getClient() {
        return client;
    }

    private static List<Consumer<BrokerClientBuilder>> buildActions;

    private Broker() {}

    /** 设置 broker 客户端 */
    public static void setClient(BrokerClient client) {
        if (Broker.client != null) {
            throw new UnsupportedOperationException("Cannot redefine singleton instance");
        }
        Broker.buildActions = null;
        Broker.client = client;
    }

    /**
     * 获取客户端信息
     */
    public static BrokerClientInfo getClientInfo() {
        return client.getClientInfo();
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
     * 注入 brokerClient
     *
     * @see net.afyer.afybroker.client.aware.BrokerClientAware
     */
    public static void aware(Object object) {
        client.aware(object);
    }

    /**
     * 获取远程服务代理
     */
    public static <T> T getService(Class<T> serviceInterface) {
        return client.getService(serviceInterface);
    }

    /**
     * 获取远程服务代理（带标签选择）
     */
    public static <T> T getService(Class<T> serviceInterface, String... tags) {
        return client.getService(serviceInterface, tags);
    }

    /**
     * 添加 brokerClient 构建方法
     */
    public static void buildAction(Consumer<BrokerClientBuilder> action) {
        if (Broker.client != null) {
            throw new UnsupportedOperationException("Broker client singleton has already been defined.");
        }
        if (buildActions == null) {
            buildActions = new ArrayList<>();
        }
        buildActions.add(action);
    }

    public static List<Consumer<BrokerClientBuilder>> getBuildActions() {
        return buildActions == null ? Collections.emptyList() : buildActions;
    }
}
