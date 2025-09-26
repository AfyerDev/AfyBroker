package net.afyer.afybroker.client;

import com.alipay.remoting.ConnectionEventProcessor;
import com.alipay.remoting.ConnectionEventType;
import com.alipay.remoting.config.BoltClientOption;
import com.alipay.remoting.config.Configs;
import com.alipay.remoting.rpc.RpcClient;
import com.alipay.remoting.rpc.protocol.UserProcessor;
import net.afyer.afybroker.client.preprocessor.BrokerPreprocessor;
import net.afyer.afybroker.client.processor.RequestBrokerClientInfoClientProcessor;
import net.afyer.afybroker.client.processor.RpcInvocationClientProcessor;
import net.afyer.afybroker.client.processor.connection.CloseEventClientProcessor;
import net.afyer.afybroker.client.processor.connection.ConnectEventClientProcessor;
import net.afyer.afybroker.client.processor.connection.ConnectFailedEventClientProcessor;
import net.afyer.afybroker.client.processor.connection.ExceptionEventClientProcessor;
import net.afyer.afybroker.client.service.BrokerServiceEntry;
import net.afyer.afybroker.client.service.BrokerServiceRegistry;
import net.afyer.afybroker.core.BrokerClientInfo;
import net.afyer.afybroker.core.BrokerClientType;
import net.afyer.afybroker.core.BrokerGlobalConfig;
import net.afyer.afybroker.core.message.BrokerClientInfoMessage;
import net.afyer.afybroker.core.util.ConnectionEventTypeProcessor;

import java.util.*;

/**
 * @author Nipuru
 * @since 2022/7/31 10:10
 */
public class BrokerClientBuilder {

    /**
     * 客户端名称(唯一标识)
     */
    private String name;

    /**
     * 客户端类型
     */
    private String type = BrokerClientType.UNKNOWN;

    /**
     * broker 服务端主机
     */
    private String host = BrokerGlobalConfig.BROKER_HOST;

    /**
     * broker 服务端端口
     */
    private int port = BrokerGlobalConfig.BROKER_PORT;

    /** 消息发送超时时间 */
    private int defaultTimeoutMillis = BrokerGlobalConfig.DEFAULT_TIMEOUT_MILLIS;

    /** 客户端标签 */
    private final Set<String> tags = new HashSet<>();

    /** 客户端元数据 */
    private final Map<String, String> metadata = new HashMap<>();

    /** 用户处理器 */
    private final List<UserProcessor<?>> processorList = new ArrayList<>();

    /** bolt 连接器 */
    private final Map<ConnectionEventType, ConnectionEventProcessor> connectionEventProcessorMap = new HashMap<>();

    /** 服务注册表 */
    private final Map<String, BrokerServiceEntry> serviceMap = new HashMap<>();

    /** 预处理函数列表 */
    private final List<BrokerPreprocessor> preprocessors = new ArrayList<>();

    public String name() {
        return name;
    }

    public BrokerClientBuilder name(String name) {
        this.name = name;
        return this;
    }

    public String type() {
        return type;
    }

    public BrokerClientBuilder type(String type) {
        this.type = type;
        return this;
    }

    public String host() {
        return host;
    }

    public BrokerClientBuilder host(String host) {
        this.host = host;
        return this;
    }

    public int port() {
        return port;
    }

    public BrokerClientBuilder port(int port) {
        this.port = port;
        return this;
    }

    public int defaultTimeoutMillis() {
        return defaultTimeoutMillis;
    }

    public BrokerClientBuilder defaultTimeoutMillis(int defaultTimeoutMillis) {
        this.defaultTimeoutMillis = defaultTimeoutMillis;
        return this;
    }

    BrokerClientBuilder() {
        // 初始化一些处理器
        this.defaultProcessor();

        // 通过系统属性来开和关，如果一个进程有多个 RpcClient，则同时生效
        // 开启 bolt 重连
        System.setProperty(Configs.CONN_MONITOR_SWITCH, "true");
        System.setProperty(Configs.CONN_RECONNECT_SWITCH, "true");
    }

    public BrokerClient build() {
        this.check();

        BrokerAddress address = new BrokerAddress(host, port);

        BrokerServiceRegistry serviceRegistry = new BrokerServiceRegistry(serviceMap);
        BrokerClientInfo clientInfo = new BrokerClientInfoMessage()
                .setName(name)
                .setType(type)
                .setTags(tags)
                .setMetadata(metadata)
                .setAddress(address.getAddress())
                .setServices(serviceRegistry.getDescriptors())
                .build();

        RpcClient rpcClient = new RpcClient();
        rpcClient.option(BoltClientOption.CONN_RECONNECT_SWITCH, true);
        rpcClient.option(BoltClientOption.CONN_MONITOR_SWITCH, true);

        BrokerClient brokerClient = new BrokerClient();
        brokerClient.setClientInfo(clientInfo);
        brokerClient.setRpcClient(rpcClient);
        brokerClient.setServiceRegistry(serviceRegistry);
        brokerClient.setDefaultTimeoutMillis(defaultTimeoutMillis);
        brokerClient.setPreprocessors(preprocessors);


        this.processorList.forEach(brokerClient::aware);
        this.processorList.forEach(rpcClient::registerUserProcessor);
        this.connectionEventProcessorMap.forEach(rpcClient::addConnectionEventProcessor);
        this.connectionEventProcessorMap.values().forEach(brokerClient::aware);

        return brokerClient;
    }

    private void check() {
        if (name == null || name.isEmpty()) {
            name = String.format("%s-%s", BrokerClientType.UNKNOWN,
                    UUID.randomUUID().toString().substring(0, 8));
        }

        if (type == null) {
            throw new RuntimeException("BrokerClientType cannot be null");
        }
    }

    /**
     * 添加客户端标签
     *
     * @param tag 客户端标签
     * @return this
     */
    public BrokerClientBuilder addTag(String tag) {
        if (tag != null) {
            tags.add(tag);
        }
        return this;
    }

    /**
     * 添加客户端标签
     *
     * @param tags 客户端标签
     * @return this
     */
    public BrokerClientBuilder addTags(String... tags) {
        this.tags.addAll(Arrays.asList(tags));
        return this;
    }

    /**
     * 添加客户端标签
     *
     * @param tags 客户端标签
     * @return this
     */
    public BrokerClientBuilder addTags(Collection<String> tags) {
        this.tags.addAll(tags);
        return this;
    }

    /**
     * 移除所有客户端标签
     *
     * @return this
     */
    public BrokerClientBuilder clearTags() {
        this.tags.clear();
        return this;
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
     * 注册连接器
     *
     * @param processor  processor
     * @return this
     */
    public BrokerClientBuilder addConnectionEventProcessor(ConnectionEventTypeProcessor processor) {
        this.connectionEventProcessorMap.put(processor.getType(), processor);
        return this;
    }

    /**
     * 移除所有默认 处理器
     *
     * @return this
     */
    public BrokerClientBuilder clearProcessors() {
        this.processorList.clear();
        this.connectionEventProcessorMap.clear();
        return this;
    }

    /**
     * 添加客户端元数据
     *
     * @return this
     */
    public BrokerClientBuilder addMetadata(String key, String value) {
        this.metadata.put(key, value);
        return this;
    }

    /**
     * 添加客户端元数据
     *
     * @return this
     */
    public BrokerClientBuilder addMetadata(Map<String, String> metadata) {
        this.metadata.putAll(metadata);
        return this;
    }

    /**
     * 移除所有客户端元数据
     *
     * @return this
     */
    public BrokerClientBuilder clearMetadata() {
        this.metadata.clear();
        return this;
    }

    /**
     * 注册服务实现（带标签）
     */
    public <T> BrokerClientBuilder registerService(Class<T> serviceInterface, T serviceImpl, String... tags) {
        String interfaceName = serviceInterface.getName();
        BrokerServiceEntry entry = new BrokerServiceEntry(serviceInterface, serviceImpl, new HashSet<>(Arrays.asList(tags)));
        serviceMap.put(interfaceName, entry);
        return this;
    }

    /**
     * 注册预处理函数
     * 在每次远程调用前执行，用于安全检查、权限验证等
     * 
     * @param preprocessor 预处理函数
     * @return this
     */
    public BrokerClientBuilder registerPreprocessor(BrokerPreprocessor preprocessor) {
        this.preprocessors.add(preprocessor);
        return this;
    }

    /**
     * 清除所有预处理函数
     * 
     * @return this
     */
    public BrokerClientBuilder clearPreprocessors() {
        this.preprocessors.clear();
        return this;
    }

    private void defaultProcessor() {
        this
                .addConnectionEventProcessor(ConnectionEventType.CONNECT, new ConnectEventClientProcessor())
                .addConnectionEventProcessor(ConnectionEventType.CLOSE, new CloseEventClientProcessor())
                .addConnectionEventProcessor(ConnectionEventType.CONNECT_FAILED, new ConnectFailedEventClientProcessor())
                .addConnectionEventProcessor(ConnectionEventType.EXCEPTION, new ExceptionEventClientProcessor());

        this
                .registerUserProcessor(new RequestBrokerClientInfoClientProcessor())
                .registerUserProcessor(new RpcInvocationClientProcessor());
    }



}
