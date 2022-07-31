package net.afyer.afybroker.server.proxy;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import net.afyer.afybroker.core.BrokerClientType;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 客户端代理 管理器
 *
 * @author Nipuru
 * @since 2022/7/31 8:00
 */
@FieldDefaults(level = AccessLevel.PRIVATE)
public class BrokerClientProxyManager {

    final Map<String, BrokerClientProxy> byAddress = new ConcurrentHashMap<>();
    final Map<String, BrokerClientProxy> view = Collections.unmodifiableMap(byAddress);


    /** 注册客户端代理 */
    public void register(BrokerClientProxy brokerClientProxy) {
        String address = brokerClientProxy.getAddress();

        byAddress.put(address, brokerClientProxy);
    }

    /** 移除客户端代理 */
    public void remove(String address) {
        byAddress.remove(address);
    }

    /** 通过名称（唯一标识）获取客户端代理 */
    public BrokerClientProxy getByName(String name) {
        for (BrokerClientProxy brokerClientProxy : byAddress.values()) {
            if (brokerClientProxy.getName().equalsIgnoreCase(name)) {
                return brokerClientProxy;
            }
        }
        return null;
    }

    /** 通过标签获取客户端代理 */
    public List<BrokerClientProxy> getByTag(String tag) {
        List<BrokerClientProxy> list = new ArrayList<>(16);

        for (BrokerClientProxy brokerClientProxy : byAddress.values()) {
            if (brokerClientProxy.getTag() != null && brokerClientProxy.getTag().equalsIgnoreCase(tag)) {
                list.add(brokerClientProxy);
            }
        }

        return list;
    }

    /** 通过类型获取客户端代理 */
    public List<BrokerClientProxy> getByType(BrokerClientType type) {
        List<BrokerClientProxy> list = new ArrayList<>(16);

        for (BrokerClientProxy brokerClientProxy : byAddress.values()) {
            if (brokerClientProxy.getType() == type) {
                list.add(brokerClientProxy);
            }
        }

        return list;
    }

    /** 获取客户端代理集合 */
    public Collection<BrokerClientProxy> list() {
        return view.values();
    }

}
