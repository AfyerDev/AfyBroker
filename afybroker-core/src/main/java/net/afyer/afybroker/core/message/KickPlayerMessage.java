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
 * 踢出玩家消息
 *
 * @author Nipuru
 * @since 2022/10/10 10:30
 */
@Getter
@Setter
@ToString
@Accessors(chain = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
public class KickPlayerMessage implements Serializable {
    private static final long serialVersionUID = 225514412094976346L;

    /** 玩家名 */
    UUID uniqueId;

    /** 踢出消息 */
    String message;
}
