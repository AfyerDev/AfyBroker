package net.afyer.afybroker.server.proxy;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Predicate;

/**
 * 客户端代理 管理器
 *
 * @author Nipuru
 * @since 2022/7/31 8:00
 */
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE)
public class BrokerClientProxyManager {

    final Map<String, BrokerClientProxy> byAddress = new ConcurrentHashMap<>();

    /** 注册客户端代理 */
    public void register(BrokerClientProxy brokerClientProxy) {
        String address = brokerClientProxy.getAddress();

        byAddress.put(address, brokerClientProxy);
    }

    /** 移除客户端代理 */
    public void remove(String address) {
        byAddress.remove(address);
    }

    /** 通过地址获取客户端代理 */
    @Nullable
    public BrokerClientProxy getByAddress(String address) {
        return byAddress.get(address);
    }

    /** 通过名称（唯一标识）获取客户端代理 */
    @Nullable
    public BrokerClientProxy getByName(String name) {
        for (BrokerClientProxy brokerClientProxy : byAddress.values()) {
            if (brokerClientProxy.getName().equalsIgnoreCase(name)) {
                return brokerClientProxy;
            }
        }
        return null;
    }

    /** 通过自定义过滤器获取客户端代理 */
    public List<BrokerClientProxy> getByFilter(Predicate<BrokerClientProxy> filter){
        List<BrokerClientProxy> list = new ArrayList<>();

        for(BrokerClientProxy client : byAddress.values()){
            if(filter.test(client)){
                list.add(client);
            }
        }

        return list;
    }

    /** 通过标签获取客户端代理 */
    public List<BrokerClientProxy> getByTag(String tag) {
        return this.getByFilter(clientProxy -> clientProxy.hasTag(tag));
    }

    /** 通过标签获取客户端代理 */
    public List<BrokerClientProxy> getByAnyTags(String... tags) {
        return this.getByFilter(clientProxy -> clientProxy.hasAnyTags(tags));
    }

    public List<BrokerClientProxy> getByAnyTags(Iterable<String> tags) {
        return this.getByFilter(clientProxy -> clientProxy.hasAnyTags(tags));
    }

    public List<BrokerClientProxy> getByAllTags(String... tags) {
        return this.getByFilter(clientProxy -> clientProxy.hasAllTags(tags));
    }

    public List<BrokerClientProxy> getByAllTags(Iterable<String> tags) {
        return this.getByFilter(clientProxy -> clientProxy.hasAllTags(tags));
    }

    /** 通过类型获取客户端代理 */
    public List<BrokerClientProxy> getByType(String type) {
        return this.getByFilter(clientProxy -> Objects.equals(clientProxy.getType(), type));
    }

    /**
     * 获取客户端代理集合
     */
    public List<BrokerClientProxy> list() {
        return new ArrayList<>(byAddress.values());
    }

}
