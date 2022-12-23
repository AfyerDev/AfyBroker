package net.afyer.afybroker.core;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import net.afyer.afybroker.core.message.BrokerClientInfoMessage;

import java.util.HashSet;
import java.util.Set;

/**
 * @author Nipuru
 * @since 2022/12/23 10:11
 */
@Getter
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class BrokerClientInfo {

    /** 客户端名称(唯一标识) */
    final String name;
    /** 客户端标签 */
    final Set<String> tags;
    /** 客户端类型 */
    final BrokerClientType type;
    /** 客户端地址 */
    final String address;

    public BrokerClientInfoMessage toMessage() {
        return new BrokerClientInfoMessage()
                .setName(name)
                .setTags(new HashSet<>(tags))
                .setType(type)
                .setAddress(address);
    }
}
