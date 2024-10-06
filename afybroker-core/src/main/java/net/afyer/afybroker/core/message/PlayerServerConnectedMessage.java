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
 * 玩家与 minecraft 服务器连接状态变化的消息
 * @author Nipuru
 * @since 2022/9/12 12:21
 */
@Getter
@Setter
@ToString
@Accessors(chain = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PlayerServerConnectedMessage implements Serializable {
    private static final long serialVersionUID = 5436035428469761938L;

    /** 玩家uuid */
    UUID uniqueId;

    /** 玩家名 */
    String name;

    /** 服务器名 */
    String serverName;

}
