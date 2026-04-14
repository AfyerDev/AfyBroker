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
import net.afyer.afybroker.core.observability.*;
import net.afyer.afybroker.core.util.ConnectionEventTypeProcessor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public class BrokerClientBuilder {

    private String name;
    private String type = BrokerClientType.UNKNOWN;
    private String host = BrokerGlobalConfig.BROKER_HOST;
    private int port = BrokerGlobalConfig.BROKER_PORT;
    private int defaultTimeoutMillis = BrokerGlobalConfig.DEFAULT_TIMEOUT_MILLIS;
    private final Set<String> tags = new HashSet<>();
    private final Map<String, String> metadata = new HashMap<>();
    private final List<UserProcessor<?>> processorList = new ArrayList<>();
    private final Map<ConnectionEventType, ConnectionEventProcessor> connectionEventProcessorMap = new HashMap<>();
    private final Map<String, BrokerServiceEntry> serviceMap = new HashMap<>();
    private final List<BrokerPreprocessor> preprocessors = new ArrayList<>();
    private final List<Observability> observabilities = new ArrayList<>();

    BrokerClientBuilder() {
        defaultProcessor();
        System.setProperty(Configs.CONN_MONITOR_SWITCH, "true");
        System.setProperty(Configs.CONN_RECONNECT_SWITCH, "true");
    }

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

    public BrokerClient build() {
        check();

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
        brokerClient.setObservability(CompositeObservability.of(
                observabilities.toArray(new Observability[0])));

        processorList.forEach(brokerClient::aware);
        processorList.forEach(rpcClient::registerUserProcessor);
        connectionEventProcessorMap.forEach(rpcClient::addConnectionEventProcessor);
        connectionEventProcessorMap.values().forEach(brokerClient::aware);

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

    public BrokerClientBuilder addTag(String tag) {
        if (tag != null) {
            tags.add(tag);
        }
        return this;
    }

    public BrokerClientBuilder addTags(String... tags) {
        this.tags.addAll(Arrays.asList(tags));
        return this;
    }

    public BrokerClientBuilder addTags(Collection<String> tags) {
        this.tags.addAll(tags);
        return this;
    }

    public BrokerClientBuilder clearTags() {
        this.tags.clear();
        return this;
    }

    public BrokerClientBuilder registerUserProcessor(UserProcessor<?> processor) {
        this.processorList.add(processor);
        return this;
    }

    public BrokerClientBuilder addConnectionEventProcessor(ConnectionEventType type, ConnectionEventProcessor processor) {
        this.connectionEventProcessorMap.put(type, processor);
        return this;
    }

    public BrokerClientBuilder addConnectionEventProcessor(ConnectionEventTypeProcessor processor) {
        this.connectionEventProcessorMap.put(processor.getType(), processor);
        return this;
    }

    public BrokerClientBuilder clearProcessors() {
        this.processorList.clear();
        this.connectionEventProcessorMap.clear();
        return this;
    }

    public BrokerClientBuilder addMetadata(String key, String value) {
        this.metadata.put(key, value);
        return this;
    }

    public BrokerClientBuilder addMetadata(Map<String, String> metadata) {
        this.metadata.putAll(metadata);
        return this;
    }

    public BrokerClientBuilder clearMetadata() {
        this.metadata.clear();
        return this;
    }

    public <T> BrokerClientBuilder registerService(Class<T> serviceInterface, T serviceImpl, String... tags) {
        String interfaceName = serviceInterface.getName();
        BrokerServiceEntry entry = new BrokerServiceEntry(serviceInterface, serviceImpl, new HashSet<>(Arrays.asList(tags)));
        serviceMap.put(interfaceName, entry);
        return this;
    }

    public BrokerClientBuilder registerPreprocessor(BrokerPreprocessor preprocessor) {
        this.preprocessors.add(preprocessor);
        return this;
    }

    public BrokerClientBuilder clearPreprocessors() {
        this.preprocessors.clear();
        return this;
    }

    public BrokerClientBuilder observability(Observability observability) {
        if (observability != null && observability != Observability.NOOP) {
            this.observabilities.add(observability);
        }
        return this;
    }

    public BrokerClientBuilder clearObservability() {
        this.observabilities.clear();
        return this;
    }

    public BrokerClientBuilder enablePrometheus(PrometheusObservabilityOptions options) {
        try {
            return observability(new PrometheusObservability(Role.CLIENT, type, options));
        } catch (Exception e) {
            throw new IllegalStateException("Failed to initialize Prometheus observability", e);
        }
    }

    private void defaultProcessor() {
        addConnectionEventProcessor(ConnectionEventType.CONNECT, new ConnectEventClientProcessor())
                .addConnectionEventProcessor(ConnectionEventType.CLOSE, new CloseEventClientProcessor())
                .addConnectionEventProcessor(ConnectionEventType.CONNECT_FAILED, new ConnectFailedEventClientProcessor())
                .addConnectionEventProcessor(ConnectionEventType.EXCEPTION, new ExceptionEventClientProcessor());

        registerUserProcessor(new RequestBrokerClientInfoClientProcessor())
                .registerUserProcessor(new RpcInvocationClientProcessor());
    }
}
