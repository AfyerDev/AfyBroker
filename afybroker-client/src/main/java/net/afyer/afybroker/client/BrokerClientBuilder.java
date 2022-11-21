package net.afyer.afybroker.client;

import com.alipay.remoting.ConnectionEventProcessor;
import com.alipay.remoting.ConnectionEventType;
import com.alipay.remoting.rpc.protocol.UserProcessor;
import lombok.AccessLevel;
import lombok.Setter;
import lombok.experimental.Accessors;
import lombok.experimental.FieldDefaults;
import net.afyer.afybroker.client.processor.RequestBrokerClientInfoClientProcessor;
import net.afyer.afybroker.client.processor.connection.CloseEventClientProcessor;
import net.afyer.afybroker.client.processor.connection.ConnectEventClientProcessor;
import net.afyer.afybroker.client.processor.connection.ConnectFailedEventClientProcessor;
import net.afyer.afybroker.client.processor.connection.ExceptionEventClientProcessor;
import net.afyer.afybroker.core.BrokerClientType;
import net.afyer.afybroker.core.BrokerGlobalConfig;
import net.afyer.afybroker.core.message.BrokerClientInfoMessage;

import java.util.*;

/**
 * @author Nipuru
 * @since 2022/7/31 10:10
 */
@Setter
@Accessors(fluent = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
public class BrokerClientBuilder {

    /**
     * 客户端名称(唯一标识)
     */
    String name = UUID.randomUUID().toString();

    /**
     * 客户端类型
     */
    BrokerClientType type;

    /**
     * 客户端标签
     */
    String tag;

    /**
     * broker 服务端主机
     */
    String host = BrokerGlobalConfig.BROKER_HOST;

    /**
     * broker 服务端端口
     */
    int port = BrokerGlobalConfig.BROKER_PORT;

    /** 用户处理器 */
    final List<UserProcessor<?>> processorList = new ArrayList<>();

    /** bolt 连接器 */
    final Map<ConnectionEventType, ConnectionEventProcessor> connectionEventProcessorMap = new HashMap<>();

    BrokerClientBuilder() {
        // 初始化一些处理器
        this.defaultProcessor();
    }

    public BrokerClient build() {
        this.check();

        BrokerAddress address = new BrokerAddress(host, port);

        BrokerClientInfoMessage clientInfo = new BrokerClientInfoMessage()
                .setName(name)
                .setType(type)
                .setTag(tag)
                .setAddress(address.getAddress());

        BrokerClient brokerClient = new BrokerClient();

        brokerClient.setClientInfo(clientInfo);

        this.processorList.forEach(brokerClient::registerUserProcessor);
        this.connectionEventProcessorMap.forEach(brokerClient::addConnectionEventProcessor);


        return brokerClient;
    }

    private void check() {
        if (type == null) {
            throw new RuntimeException("BrokerClientType cannot be null");
        }
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
                .registerUserProcessor(new RequestBrokerClientInfoClientProcessor());
    }

}
