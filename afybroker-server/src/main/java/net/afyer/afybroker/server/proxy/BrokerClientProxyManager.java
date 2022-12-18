package net.afyer.afybroker.server.proxy;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import net.afyer.afybroker.core.BrokerClientType;
import net.afyer.afybroker.server.util.BrokerClientProxies;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
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
    public BrokerClientProxies getByFilter(Predicate<BrokerClientProxy> filter){
        BrokerClientProxies list = new BrokerClientProxies();

        for(BrokerClientProxy client : byAddress.values()){
            if(filter.test(client)){
                list.add(client);
            }
        }

        return list;
    }

    /** 通过标签获取客户端代理 */
    public BrokerClientProxies getByTag(String tag) {
        BrokerClientProxies list = new BrokerClientProxies();

        for (BrokerClientProxy brokerClientProxy : byAddress.values()) {
            if (brokerClientProxy.hasTag(tag)) {
                list.add(brokerClientProxy);
            }
        }

        return list;
    }

    /** 通过类型获取客户端代理 */
    public BrokerClientProxies getByType(BrokerClientType type) {
        BrokerClientProxies list = new BrokerClientProxies();

        for (BrokerClientProxy brokerClientProxy : byAddress.values()) {
            if (brokerClientProxy.getType() == type) {
                list.add(brokerClientProxy);
            }
        }

        return list;
    }

    /**
     * 获取客户端代理集合
     */
    public BrokerClientProxies list() {
        return new BrokerClientProxies(byAddress.values());
    }

}
