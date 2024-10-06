package net.afyer.afybroker.server.event;

import com.alipay.remoting.Connection;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import net.afyer.afybroker.server.plugin.Event;

/**
 * 当 BrokerClient 初次连接至 BrokerServer 时触发此事件
 *
 * @author Nipuru
 * @since 2022/9/10 17:54
 */
@Getter
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ClientConnectEvent extends Event {
    /** 客户端地址 */
    final String remoteAddress;

    /** 客户端连接 */
    final Connection connection;
}
