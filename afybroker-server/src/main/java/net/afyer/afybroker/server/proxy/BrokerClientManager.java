package net.afyer.afybroker.server.proxy;

import org.jetbrains.annotations.Nullable;
import com.alipay.remoting.Connection;
import net.afyer.afybroker.core.BrokerClientInfo;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Predicate;

/**
 * 客户端管理器
 *
 * @author Nipuru
 * @since 2022/7/31 8:00
 */
public class BrokerClientManager {

    private final Map<String, BrokerClientItem> byAddress = new ConcurrentHashMap<>();

    private static final String CLIENT_ATTRIBUTE = BrokerClientManager.class.getName();
    private final BrokerServiceRegistry serviceRegistry;

    public BrokerClientManager(BrokerServiceRegistry serviceRegistry) {
        this.serviceRegistry = serviceRegistry;
    }

    /** 同一连接重复提交相同的注册信息时返回 false。 */
    public synchronized boolean register(BrokerClientItem client) {
        BrokerClientInfo info = client.getClientInfo();
        if (info.getName() == null || info.getName().trim().isEmpty()) {
            throw new IllegalArgumentException("client_name must not be blank");
        }
        if (info.getType() == null || info.getTags() == null || info.getMetadata() == null) {
            throw new IllegalArgumentException("Client type, tags and metadata must not be null");
        }
        BrokerServiceRegistry.keys(info.getServices());
        // CLOSE 可能先于排队的注册请求执行，防止已关闭的连接重新入表。
        if (!client.getConnection().isFine()) throw new IllegalStateException("Registration connection is closed");

        BrokerClientItem previous = (BrokerClientItem) client.getConnection().getAttribute(CLIENT_ATTRIBUTE);
        if (previous != null) {
            BrokerClientInfo old = previous.getClientInfo();
            if (!Objects.equals(old.getName(), info.getName()) || !Objects.equals(old.getType(), info.getType())
                    || !old.getTags().equals(info.getTags()) || !old.getMetadata().equals(info.getMetadata())
                    || !BrokerServiceRegistry.keys(old.getServices()).equals(BrokerServiceRegistry.keys(info.getServices()))) {
                throw new IllegalArgumentException("Cannot change registration on an existing connection");
            }
            return false;
        }
        BrokerClientItem owner = getByName(client.getName());
        if (owner != null) {
            throw new IllegalArgumentException("Duplicate client_name: " + client.getName() + "; owner=" + owner.getAddress());
        }
        serviceRegistry.registerClientServices(client, info.getServices());
        byAddress.put(client.getAddress(), client);
        client.getConnection().setAttribute(CLIENT_ATTRIBUTE, client);
        return true;
    }

    /** 仅移除属于当前连接的记录，延迟到达的 CLOSE 事件也不会影响新连接。 */
    public synchronized BrokerClientItem remove(Connection connection) {
        BrokerClientItem client = (BrokerClientItem) connection.getAttribute(CLIENT_ATTRIBUTE);
        if (client == null) return null;
        connection.removeAttribute(CLIENT_ATTRIBUTE);
        if (byAddress.remove(client.getAddress(), client)) serviceRegistry.unregisterClientServices(client);
        return client;
    }

    /**
     * 通过地址获取客户端代理
     */
    @Nullable
    public synchronized BrokerClientItem getByAddress(String address) {
        return byAddress.get(address);
    }

    /**
     * 通过名称（唯一标识）获取客户端代理
     */
    @Nullable
    public synchronized BrokerClientItem getByName(String name) {
        for (BrokerClientItem brokerClientItem : byAddress.values()) {
            if (brokerClientItem.getName().equalsIgnoreCase(name)) {
                return brokerClientItem;
            }
        }
        return null;
    }

    /**
     * 通过自定义过滤器获取客户端代理
     */
    public List<BrokerClientItem> getByFilter(Predicate<BrokerClientItem> filter) {
        List<BrokerClientItem> list = new ArrayList<>();

        for (BrokerClientItem client : list()) {
            if (filter.test(client)) {
                list.add(client);
            }
        }

        return list;
    }

    /**
     * 通过标签获取客户端代理
     */
    public List<BrokerClientItem> getByTag(String tag) {
        return this.getByFilter(clientProxy -> clientProxy.hasTag(tag));
    }

    /**
     * 通过标签获取客户端代理
     */
    public List<BrokerClientItem> getByAnyTags(String... tags) {
        return this.getByFilter(clientProxy -> clientProxy.hasAnyTags(tags));
    }

    public List<BrokerClientItem> getByAnyTags(Iterable<String> tags) {
        return this.getByFilter(clientProxy -> clientProxy.hasAnyTags(tags));
    }

    public List<BrokerClientItem> getByAllTags(String... tags) {
        return this.getByFilter(clientProxy -> clientProxy.hasAllTags(tags));
    }

    public List<BrokerClientItem> getByAllTags(Iterable<String> tags) {
        return this.getByFilter(clientProxy -> clientProxy.hasAllTags(tags));
    }

    /**
     * 通过类型获取客户端代理
     */
    public List<BrokerClientItem> getByType(String type) {
        return this.getByFilter(clientProxy -> Objects.equals(clientProxy.getType(), type));
    }

    /**
     * 获取客户端代理集合
     */
    public synchronized List<BrokerClientItem> list() {
        return new ArrayList<>(byAddress.values());
    }

}
