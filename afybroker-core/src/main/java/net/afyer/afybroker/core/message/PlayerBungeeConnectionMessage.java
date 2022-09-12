package net.afyer.afybroker.core.message;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;
import lombok.experimental.FieldDefaults;

import java.io.Serial;
import java.io.Serializable;
import java.util.UUID;

/**
 * 玩家与 bungee 连接状态变化的消息
 *
 * @author Nipuru
 * @since 2022/8/1 11:32
 */
@Getter
@Setter
@ToString
@Accessors(chain = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PlayerBungeeConnectionMessage implements Serializable {
    @Serial
    private static final long serialVersionUID = 1791475059445212432L;

    /** bungee 连接的标识 */
    public static final byte CONNECT = 1;

    /** bungee 断开连接的标识 */
    public static final byte DISCONNECT = 0;

    /** 玩家uid */
    UUID uid;

    /** 玩家名 */
    String name;

    /** 状态标识符 */
    byte state;

}
