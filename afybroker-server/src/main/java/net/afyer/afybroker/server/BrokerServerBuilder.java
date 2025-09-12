package net.afyer.afybroker.server;

import com.alipay.remoting.ConnectionEventProcessor;
import com.alipay.remoting.ConnectionEventType;
import com.alipay.remoting.config.Configs;
import com.alipay.remoting.rpc.protocol.UserProcessor;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import lombok.AccessLevel;
import lombok.Setter;
import lombok.experimental.Accessors;
import lombok.experimental.FieldDefaults;
import net.afyer.afybroker.core.BrokerGlobalConfig;
import net.afyer.afybroker.server.plugin.BrokerClassLoader;
import net.afyer.afybroker.server.processor.*;
import net.afyer.afybroker.server.processor.connection.CloseEventBrokerProcessor;
import net.afyer.afybroker.server.processor.connection.ConnectEventBrokerProcessor;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author Nipuru
 * @since 2022/7/29 20:13
 */
@Setter
@Accessors(fluent = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
public class BrokerServerBuilder {

    /**
     * broker 端口
     */
    int port = BrokerGlobalConfig.BROKER_PORT;

    /**
     * 用户处理器
     */
    final List<UserProcessor<?>> processorList = new ArrayList<>();

    /**
     * bolt 连接器
     */
    final Map<ConnectionEventType, ConnectionEventProcessor> connectionEventProcessorMap = new HashMap<>();

    BrokerServerBuilder() {
        // 初始化一些处理器
        this.defaultProcessor();

        // 开启 bolt 重连, 通过系统属性来开和关，如果一个进程有多个 RpcClient，则同时生效
        System.setProperty(Configs.CONN_MONITOR_SWITCH, "true");
        System.setProperty(Configs.CONN_RECONNECT_SWITCH, "true");

        System.setProperty(Configs.CONN_MONITOR_SWITCH, "true");
        System.setProperty(Configs.CONN_RECONNECT_SWITCH, "true");
    }

    public BrokerServer build() throws IOException {
        this.check();

        BrokerServer brokerServer = new BrokerServer();

        brokerServer.setPort(port);

        BrokerClassLoader brokerLoader = new BrokerClassLoader();
        Thread.currentThread().setContextClassLoader(brokerLoader);
        brokerServer.initServer();

        this.processorList.forEach(brokerServer::registerUserProcessor);
        this.connectionEventProcessorMap.forEach(brokerServer::addConnectionEventProcessor);

        return brokerServer;
    }

    /**
     * 注册用户处理器
     *
     * @param processor processor
     * @return this
     */
    public BrokerServerBuilder registerUserProcessor(UserProcessor<?> processor) {
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

    private void check() {
        if (this.port <= 0) {
            throw new RuntimeException("port error!");
        }
    }

    private void defaultProcessor() {
        this
                .addConnectionEventProcessor(ConnectionEventType.CONNECT, new ConnectEventBrokerProcessor())
                .addConnectionEventProcessor(ConnectionEventType.CLOSE, new CloseEventBrokerProcessor());

        this
                .registerUserProcessor(new PlayerProxyConnectBrokerProcessor())
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
                .registerUserProcessor(new RpcInvocationBrokerProcessor());

    }



}
