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
 * 强制玩家执行指令的消息
 *
 * @author Nipuru
 * @since 2022/8/12 15:44
 */
@Getter
@Setter
@ToString
@Accessors(chain = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
public class SudoMessage implements Serializable {
    @Serial
    private static final long serialVersionUID = -2417851615120804419L;

    /**
     * 玩家名
     */
    String player;

    /**
     * bukkit/bungee
     */
    BrokerClientType type;

    /**
     * 指令
     */
    String command;

}
