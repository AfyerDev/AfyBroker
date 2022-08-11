package net.afyer.afybroker.core.message;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;
import lombok.experimental.FieldDefaults;
import net.afyer.afybroker.core.BrokerClientType;

import java.io.Serial;
import java.io.Serializable;

/**
 * @author Nipuru
 * @since 2022/8/5 10:04
 */
@Getter
@Setter
@ToString
@Accessors(chain = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
public class SendPlayerChatMessage implements Serializable {
    @Serial
    private static final long serialVersionUID = -4207075992906096144L;

    /**
     * 玩家名
     */
    String player;

    /**
     * 消息处理端的类型
     */
    BrokerClientType type = BrokerClientType.BUKKIT;

    /**
     * 消息
     */
    String message;

}
