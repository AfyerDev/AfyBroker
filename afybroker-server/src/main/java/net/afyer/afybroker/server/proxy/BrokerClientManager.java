package net.afyer.afybroker.server.proxy;

import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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

    private static final Logger LOGGER = LoggerFactory.getLogger(BrokerClientManager.class);

    private final Map<String, BrokerClientItem> byAddress = new ConcurrentHashMap<>();

    /** 注册客户端代理 */
    public void register(BrokerClientItem brokerClientItem) {
        String address = brokerClientItem.getAddress();

        byAddress.put(address, brokerClientItem);
    }

    /** 移除客户端代理 */
    public void remove(String address) {
        byAddress.remove(address);
    }

    /** 通过地址获取客户端代理 */
    @Nullable
    public BrokerClientItem getByAddress(String address) {
        return byAddress.get(address);
    }

    /** 通过名称（唯一标识）获取客户端代理 */
    @Nullable
    public BrokerClientItem getByName(String name) {
        for (BrokerClientItem brokerClientItem : byAddress.values()) {
            if (brokerClientItem.getName().equalsIgnoreCase(name)) {
                return brokerClientItem;
            }
        }
        return null;
    }

    /** 通过自定义过滤器获取客户端代理 */
    public List<BrokerClientItem> getByFilter(Predicate<BrokerClientItem> filter){
        List<BrokerClientItem> list = new ArrayList<>();

        for(BrokerClientItem client : byAddress.values()){
            if(filter.test(client)){
                list.add(client);
            }
        }

        return list;
    }

    /** 通过标签获取客户端代理 */
    public List<BrokerClientItem> getByTag(String tag) {
        return this.getByFilter(clientProxy -> clientProxy.hasTag(tag));
    }

    /** 通过标签获取客户端代理 */
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

    /** 通过类型获取客户端代理 */
    public List<BrokerClientItem> getByType(String type) {
        return this.getByFilter(clientProxy -> Objects.equals(clientProxy.getType(), type));
    }

    /**
     * 获取客户端代理集合
     */
    public List<BrokerClientItem> list() {
        return new ArrayList<>(byAddress.values());
    }

}
