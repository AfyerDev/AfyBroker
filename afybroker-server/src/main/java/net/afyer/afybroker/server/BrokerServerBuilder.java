package net.afyer.afybroker.server;

import com.alipay.remoting.ConnectionEventProcessor;
import com.alipay.remoting.ConnectionEventType;
import com.alipay.remoting.config.Configs;
import com.alipay.remoting.rpc.protocol.UserProcessor;
import net.afyer.afybroker.core.BrokerGlobalConfig;
import net.afyer.afybroker.core.observability.Role;
import net.afyer.afybroker.core.observability.*;
import net.afyer.afybroker.server.processor.AttributeBrokerProcessor;
import net.afyer.afybroker.server.processor.BroadcastChatBrokerProcessor;
import net.afyer.afybroker.server.processor.CloseBrokerClientBrokerProcessor;
import net.afyer.afybroker.server.processor.ConnectToServerBrokerProcessor;
import net.afyer.afybroker.server.processor.ForwardingMessageBrokerProcessor;
import net.afyer.afybroker.server.processor.KickPlayerBrokerProcessor;
import net.afyer.afybroker.server.processor.PlayerProfilePropertyBrokerProcessor;
import net.afyer.afybroker.server.processor.PlayerProxyConnectBrokerProcessor;
import net.afyer.afybroker.server.processor.PlayerProxyDisconnectBrokerProcessor;
import net.afyer.afybroker.server.processor.PlayerServerConnectedBrokerProcessor;
import net.afyer.afybroker.server.processor.PlayerServerJoinBrokerProcessor;
import net.afyer.afybroker.server.processor.RpcInvocationBrokerProcessor;
import net.afyer.afybroker.server.processor.SendPlayerChatBrokerProcessor;
import net.afyer.afybroker.server.processor.SendPlayerTitleBrokerProcessor;
import net.afyer.afybroker.server.processor.connection.CloseEventBrokerProcessor;
import net.afyer.afybroker.server.processor.connection.ConnectEventBrokerProcessor;
import net.afyer.afybroker.server.processor.connection.ConnectFailedEventBrokerProcessor;
import net.afyer.afybroker.server.processor.connection.ExceptionEventBrokerProcessor;

import java.io.IOException;
import java.util.*;

/**
 * @author Nipuru
 * @since 2022/7/29 20:13
 */
public class BrokerServerBuilder {

    /**
     * broker 地址
     */
    private String host = BrokerGlobalConfig.BROKER_HOST;
    /**
     * broker 端口
     */
    private int port = BrokerGlobalConfig.BROKER_PORT;

    /**
     * 用户处理器
     */
    private final List<UserProcessor<?>> processorList = new ArrayList<>();

    /**
     * bolt 连接器
     */
    private final Map<ConnectionEventType, ConnectionEventProcessor> connectionEventProcessorMap = new HashMap<>();

    /**
     * 指标收集器
     */
    private final List<Observability> observabilityList = new ArrayList<>();

    BrokerServerBuilder() {
        // 初始化一些处理器
        defaultProcessor();

        // 通过系统属性来开和关，如果一个进程有多个 RpcServer，则同时生效
        // 开启 bolt 重连
        System.setProperty(Configs.CONN_MONITOR_SWITCH, "true");
        System.setProperty(Configs.CONN_RECONNECT_SWITCH, "true");
    }

    public BrokerServerBuilder host(String host) {
        this.host = host;
        return this;
    }

    public BrokerServerBuilder port(int port) {
        this.port = port;
        return this;
    }

    public BrokerServer build() throws IOException {
        BrokerServer brokerServer = new BrokerServer();
        brokerServer.setHost(host);
        brokerServer.setPort(port);
        brokerServer.setObservability(CompositeObservability.of(
                observabilityList.toArray(new Observability[0])));
        brokerServer.initServer();

        processorList.forEach(brokerServer::registerUserProcessor);
        connectionEventProcessorMap.forEach(brokerServer::addConnectionEventProcessor);

        return brokerServer;
    }

    /**
     * 注册用户处理器
     *
     * @param processor processor
     * @return this
     */
    public BrokerServerBuilder registerUserProcessor(UserProcessor<?> processor) {
        Objects.requireNonNull(processor);
        this.processorList.add(processor);
        return this;
    }

    /**
     * 注册连接器
     *
     * @param type       type
     * @param processor  processor
     * @return this
     */
    public BrokerServerBuilder addConnectionEventProcessor(ConnectionEventType type, ConnectionEventProcessor processor) {
        Objects.requireNonNull(type);
        Objects.requireNonNull(processor);
        this.connectionEventProcessorMap.put(type, processor);
        return this;
    }

    /**
     * 移除所有默认 处理器
     *
     * @return this
     */
    public BrokerServerBuilder clearProcessors() {
        this.processorList.clear();
        this.connectionEventProcessorMap.clear();
        return this;
    }

    public BrokerServerBuilder observability(Observability observability) {
        Objects.requireNonNull(observability);
        if (observability != Observability.NOOP) {
            this.observabilityList.add(observability);
        }
        return this;
    }

    public BrokerServerBuilder clearObservability() {
        this.observabilityList.clear();
        return this;
    }

    public BrokerServerBuilder enablePrometheus(PrometheusObservabilityOptions options) {
        try {
            return observability(new PrometheusObservability(Role.SERVER, "broker-server", options));
        } catch (Exception e) {
            throw new IllegalStateException("Failed to initialize Prometheus observability", e);
        }
    }

    private void defaultProcessor() {
        addConnectionEventProcessor(ConnectionEventType.CONNECT, new ConnectEventBrokerProcessor())
                .addConnectionEventProcessor(ConnectionEventType.CLOSE, new CloseEventBrokerProcessor())
                .addConnectionEventProcessor(ConnectionEventType.EXCEPTION, new ExceptionEventBrokerProcessor())
                .addConnectionEventProcessor(ConnectionEventType.CONNECT_FAILED, new ConnectFailedEventBrokerProcessor());

        registerUserProcessor(new PlayerProxyConnectBrokerProcessor())
                .registerUserProcessor(new PlayerProxyDisconnectBrokerProcessor())
                .registerUserProcessor(new SendPlayerChatBrokerProcessor())
                .registerUserProcessor(new BroadcastChatBrokerProcessor())
                .registerUserProcessor(new SendPlayerTitleBrokerProcessor())
                .registerUserProcessor(new ForwardingMessageBrokerProcessor())
                .registerUserProcessor(new ConnectToServerBrokerProcessor())
                .registerUserProcessor(new PlayerServerConnectedBrokerProcessor())
                .registerUserProcessor(new PlayerServerJoinBrokerProcessor())
                .registerUserProcessor(new KickPlayerBrokerProcessor())
                .registerUserProcessor(new PlayerProfilePropertyBrokerProcessor())
                .registerUserProcessor(new RpcInvocationBrokerProcessor())
                .registerUserProcessor(new CloseBrokerClientBrokerProcessor())
                .registerUserProcessor(new AttributeBrokerProcessor());
    }
}
