package net.afyer.afybroker.server.proxy;

import com.alipay.remoting.exception.RemotingException;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import net.afyer.afybroker.core.BrokerClientType;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

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

    /** 通过地址获取客户端代理 */
    public BrokerClientProxy getByAddress(String address) {
        return byAddress.get(address);
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

    /**
     * 获取客户端代理集合
     */
    public Collection<BrokerClientProxy> list() {
        return view.values();
    }

    /** 广播消息给全部客户端 */
    public void broadcast(Object request) {
        this.broadcast(null, request);
    }

    /** 广播消息给指定类型客户端 */
    public void broadcast(BrokerClientType clientType, Object request) {
        for (BrokerClientProxy brokerClient : new ArrayList<>(list())) {
            if (clientType == null || brokerClient.getType() == clientType) {
                try {
                    brokerClient.oneway(request);
                } catch (RemotingException | InterruptedException e) {
                    log.error(e.getMessage(), e);
                }
            }
        }
    }

}
