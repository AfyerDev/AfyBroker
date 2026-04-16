package net.afyer.afybroker.server.event;

import net.afyer.afybroker.core.message.BrokerClientInfoMessage;
import net.afyer.afybroker.server.plugin.Event;
import net.afyer.afybroker.server.proxy.BrokerClientItem;

/**
 * 当 BrokerClient 注册到 BrokerServer之后触发此事件
 *
 * @author Nipuru
 * @since 2022/9/10 17:56
 */
public class ClientRegisterEvent extends Event {

    /**
     * 客户端信息
     */
    private final BrokerClientInfoMessage clientInfo;

    /**
     * 客户端代理
     */
    private final BrokerClientItem client;

    public ClientRegisterEvent(BrokerClientInfoMessage clientInfo, BrokerClientItem client) {
        this.clientInfo = clientInfo;
        this.client = client;
    }

    public BrokerClientInfoMessage getClientInfo() {
        return clientInfo;
    }

    public BrokerClientItem getClient() {
        return client;
    }
}
