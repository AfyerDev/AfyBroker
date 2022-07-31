package net.afyer.afybroker.client;

import com.alipay.remoting.ConnectionEventProcessor;
import com.alipay.remoting.ConnectionEventType;
import com.alipay.remoting.rpc.RpcClient;
import com.alipay.remoting.rpc.protocol.UserProcessor;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import lombok.AccessLevel;
import lombok.Setter;
import lombok.experimental.Accessors;
import lombok.experimental.FieldDefaults;
import net.afyer.afybroker.client.processor.RequestBrokerClientInfoMessageClientProcessor;
import net.afyer.afybroker.client.processor.connection.CloseEventClientProcessor;
import net.afyer.afybroker.client.processor.connection.ConnectEventClientProcessor;
import net.afyer.afybroker.client.processor.connection.ConnectFailedEventClientProcessor;
import net.afyer.afybroker.client.processor.connection.ExceptionEventClientProcessor;
import net.afyer.afybroker.core.BrokerClientInfoMessage;
import net.afyer.afybroker.core.BrokerClientType;
import net.afyer.afybroker.core.BrokerGlobalConfig;

import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author Nipuru
 * @since 2022/7/31 10:10
 */
@Setter
@Accessors(fluent = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
public class BrokerClientBuilder {

    /** 客户端名称(唯一标识) */
    String name = UUID.randomUUID().toString();

    /** 客户端类型 */
    BrokerClientType type = BrokerClientType.OTHER;

    /** 客户端标签 */
    String tag;

    /** 事务线程池 */
    ExecutorService bizThread;

    /** broker 服务端主机 */
    String host = BrokerGlobalConfig.brokerHost;

    /** broker 服务端端口 */
    int port = BrokerGlobalConfig.brokerPort;

    /** broker */
    final BrokerClient brokerClient = new BrokerClient();

    /** 用户处理器 */
    final List<UserProcessor<?>> processorList = new ArrayList<>();

    /** bolt 连接器 */
    final Map<ConnectionEventType, ConnectionEventProcessor> connectionEventProcessorMap = new HashMap<>();

    BrokerClientBuilder() {
        // 初始化一些处理器
        this.defaultProcessor();
    }

    public BrokerClient build() {

        if (bizThread == null) {
            bizThread = new ThreadPoolExecutor(0, Integer.MAX_VALUE,
                    60L, TimeUnit.SECONDS,
                    new SynchronousQueue<>(),
                    new ThreadFactoryBuilder().setNameFormat("BrokerServer Pool Thread %d").build());
        }

        BrokerAddress address = new BrokerAddress(host, port);

        BrokerClientInfoMessage clientInfo = new BrokerClientInfoMessage()
                .setName(name)
                .setType(type)
                .setTag(tag)
                .setAddress(address.getAddress());

        brokerClient.setBizThread(bizThread);
        brokerClient.setClientInfo(clientInfo);

        RpcClient rpcClient = brokerClient.getRpcClient();

        this.processorList.forEach(rpcClient::registerUserProcessor);
        this.connectionEventProcessorMap.forEach(rpcClient::addConnectionEventProcessor);


        return brokerClient;
    }

    /**
     * 注册用户处理器
     *
     * @param processor processor
     * @return this
     */
    public BrokerClientBuilder registerUserProcessor(UserProcessor<?> processor) {
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
    public BrokerClientBuilder addConnectionEventProcessor(ConnectionEventType type, ConnectionEventProcessor processor) {
        this.connectionEventProcessorMap.put(type, processor);
        return this;
    }

    /**
     * 移除所有默认 处理器
     *
     * @return this
     */
    public BrokerClientBuilder clearProcessor() {
        this.processorList.clear();
        this.connectionEventProcessorMap.clear();
        return this;
    }

    private void defaultProcessor() {
        this
                .addConnectionEventProcessor(ConnectionEventType.CONNECT, new ConnectEventClientProcessor())
                .addConnectionEventProcessor(ConnectionEventType.CLOSE, new CloseEventClientProcessor())
                .addConnectionEventProcessor(ConnectionEventType.CONNECT_FAILED, new ConnectFailedEventClientProcessor())
                .addConnectionEventProcessor(ConnectionEventType.EXCEPTION, new ExceptionEventClientProcessor());

        this
                .registerUserProcessor(new RequestBrokerClientInfoMessageClientProcessor(brokerClient));
    }

}
