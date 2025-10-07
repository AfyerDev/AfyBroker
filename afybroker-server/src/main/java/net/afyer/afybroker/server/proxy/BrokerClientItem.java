package net.afyer.afybroker.server.proxy;

import com.alipay.remoting.InvokeCallback;
import com.alipay.remoting.exception.RemotingException;
import com.alipay.remoting.rpc.RpcResponseFuture;
import com.alipay.remoting.rpc.RpcServer;
import net.afyer.afybroker.core.BrokerClientInfo;
import net.afyer.afybroker.core.BrokerGlobalConfig;
import net.afyer.afybroker.core.message.BrokerClientInfoMessage;
import net.afyer.afybroker.core.message.CloseBrokerClientMessage;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.Set;

/**
 * 客户端信息
 *
 * @author Nipuru
 * @since 2022/7/30 16:15
 */
public class BrokerClientItem {

    /** 客户端信息 */
    private final BrokerClientInfo clientInfo;

    private final RpcServer rpcServer;

    /** 默认消息发送超时时间 */
    private final int defaultTimeoutMillis = BrokerGlobalConfig.DEFAULT_TIMEOUT_MILLIS;

    public BrokerClientItem(BrokerClientInfoMessage clientInfo, RpcServer rpcServer) {
        this.clientInfo = clientInfo.build();
        this.rpcServer = rpcServer;
    }

    public BrokerClientInfo getClientInfo() {
        return clientInfo;
    }

    public int getDefaultTimeoutMillis() {
        return defaultTimeoutMillis;
    }

    public String getName() {
        return clientInfo.getName();
    }

    public String getAddress() {
        return clientInfo.getAddress();
    }

    public Set<String> getTags() {
        return clientInfo.getTags();
    }

    public String getType() {
        return clientInfo.getType();
    }

    public Map<String, String> getMetadata() {
        return clientInfo.getMetadata();
    }

    @Nullable
    public String getMetadata(String key) {
        return clientInfo.getMetadata(key);
    }

    public boolean hasTag(String tag) {
        return clientInfo.hasTag(tag);
    }

    public boolean hasAnyTags(String... tags) {
        return clientInfo.hasAnyTags(tags);
    }

    public boolean hasAnyTags(Iterable<String> tags) {
        return clientInfo.hasAnyTags(tags);
    }

    public boolean hasAllTags(String... tags) {
        return clientInfo.hasAllTags(tags);
    }

    public boolean hasAllTags(Iterable<String> tags) {
        return clientInfo.hasAllTags(tags);
    }

    public <T> T invokeSync(Object request) throws RemotingException, InterruptedException {
        return invokeSync(request, defaultTimeoutMillis);
    }

    @SuppressWarnings("unchecked")
    public <T> T invokeSync(Object request, int timeoutMillis) throws RemotingException, InterruptedException {
        return (T) rpcServer.invokeSync(clientInfo.getAddress(), request, timeoutMillis);
    }

    public void oneway(Object request) throws RemotingException, InterruptedException {
        rpcServer.oneway(clientInfo.getAddress(), request);
    }

    public void invokeWithCallback(Object request, InvokeCallback invokeCallback) throws RemotingException, InterruptedException {
        invokeWithCallback(request, invokeCallback, defaultTimeoutMillis);
    }

    public void invokeWithCallback(Object request, InvokeCallback invokeCallback, int timeoutMillis) throws RemotingException, InterruptedException {
        rpcServer.invokeWithCallback(clientInfo.getAddress(), request, invokeCallback, timeoutMillis);
    }

    public RpcResponseFuture invokeWithFuture(Object request) throws RemotingException, InterruptedException {
        return invokeWithFuture(request, defaultTimeoutMillis);
    }

    public RpcResponseFuture invokeWithFuture(Object request, int timeoutMillis) throws RemotingException, InterruptedException {
        return rpcServer.invokeWithFuture(clientInfo.getAddress(), request, timeoutMillis);
    }

    public void shutdown() throws Exception {
        this.oneway(new CloseBrokerClientMessage());
    }
}
