package net.afyer.afybroker.core.message;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;
import lombok.experimental.FieldDefaults;

import java.io.Serializable;
import java.util.UUID;

/**
 * 玩家从 proxy 断开连接的消息
 * @author Nipuru
 * @since 2022/11/21 17:30
 */
@Getter
@Setter
@ToString
@Accessors(chain = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PlayerProxyDisconnectMessage implements Serializable {
    private static final long serialVersionUID = -5160344925177364814L;

    /** 玩家uuid */
    UUID uniqueId;

    /** 玩家名 */
    String name;
}
