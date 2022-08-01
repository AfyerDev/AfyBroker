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
 * @author Nipuru
 * @since 2022/8/1 11:32
 */
@Getter
@Setter
@ToString
@Accessors(chain = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
public class BrokerPlayerBungeeMessage implements Serializable {
    @Serial
    private static final long serialVersionUID = 1791475059445212432L;

    /** 玩家uid */
    UUID uid;

    /** 状态 */
    State state;

    /**
     * 发送端（客户端）名称
     */
    String clientName;

    public enum State {

        /** 连接到bc端时 */
        CONNECT,

        /** 从bc端离线时 */
        DISCONNECT,

        /** 加入bukkit服务器时 */
        JOIN
    }

}
