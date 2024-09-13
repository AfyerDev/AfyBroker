package net.afyer.afybroker.server.event;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import net.afyer.afybroker.server.plugin.Event;

import java.util.Set;

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

    /** 客户端名称 */
    final String name;

    /** 客户端标签 */
    final Set<String> tags;

    /** 客户端类型 */
    final String type;

}
