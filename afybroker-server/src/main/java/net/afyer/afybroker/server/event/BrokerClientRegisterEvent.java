package net.afyer.afybroker.server.event;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import net.afyer.afybroker.core.message.BrokerClientInfoMessage;
import net.afyer.afybroker.server.plugin.Event;
import net.afyer.afybroker.server.proxy.BrokerClientProxy;

/**
 * 当 BrokerClient 注册到 BrokerServer之后触发此事件
 *
 * @author Nipuru
 * @since 2022/9/10 17:56
 */
@Getter
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class BrokerClientRegisterEvent extends Event {

    /** 客户端信息 */
    final BrokerClientInfoMessage brokerClientInfo;

    /** 客户端代理 */
    final BrokerClientProxy brokerClientProxy;
}
