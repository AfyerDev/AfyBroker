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

    /** 服务器名称
     *  <p>
     *      当状态为BUNGEE时 此参数为玩家蹦极目标服务器名称
     *      当状态为CONNECT时 此参数为玩家所在bc服务器名称
     *  </p>
     */
    String data;

    public enum State {

        /** 连接到bc端时 */
        CONNECT,

        /** 从bc端离线时 */
        DISCONNECT,

        /** 进行蹦极时 */
        BUNGEE
    }

}
