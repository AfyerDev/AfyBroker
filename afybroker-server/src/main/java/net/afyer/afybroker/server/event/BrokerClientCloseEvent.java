package net.afyer.afybroker.server.event;

import com.alipay.remoting.Connection;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import net.afyer.afybroker.server.plugin.Event;

/**
 * 当BrokerClient与BrokerServer断开连接时触发此事件
 * @author Nipuru
 * @since 2022/9/10 17:55
 */
@Getter
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class BrokerClientCloseEvent extends Event {

    /** 客户端地址 */
    final String remoteAddress;

    /** 客户端连接 */
    final Connection connection;

}
