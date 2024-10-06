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
 * 玩家连接至 proxy 的消息
 *
 * @author Nipuru
 * @since 2022/8/1 11:32
 */
@Getter
@Setter
@ToString
@Accessors(chain = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PlayerProxyConnectMessage implements Serializable {
    private static final long serialVersionUID = 1791475059445212432L;

    /** 玩家uuid */
    UUID uniqueId;

    /** 玩家名 */
    String name;

}
