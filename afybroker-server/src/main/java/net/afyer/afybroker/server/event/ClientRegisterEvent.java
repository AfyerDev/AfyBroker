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

    /** 客户端信息 */
    private final BrokerClientInfoMessage brokerClientInfo;

    /** 客户端代理 */
    private final BrokerClientItem brokerClientItem;

    public ClientRegisterEvent(BrokerClientInfoMessage brokerClientInfo, BrokerClientItem brokerClientItem) {
        this.brokerClientInfo = brokerClientInfo;
        this.brokerClientItem = brokerClientItem;
    }

    public BrokerClientInfoMessage getBrokerClientInfo() {
        return brokerClientInfo;
    }

    public BrokerClientItem getBrokerClientItem() {
        return brokerClientItem;
    }
}
