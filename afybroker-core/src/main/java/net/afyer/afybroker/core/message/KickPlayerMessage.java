package net.afyer.afybroker.core.message;

import java.io.Serializable;
import java.util.UUID;

/**
 * 踢出玩家消息
 *
 * @author Nipuru
 * @since 2022/10/10 10:30
 */
public class KickPlayerMessage implements Serializable {
    private static final long serialVersionUID = 225514412094976346L;

    /** 玩家名 */
    private UUID uniqueId;

    /** 踢出消息 */
    private String message;

    public UUID getUniqueId() {
        return uniqueId;
    }

    public KickPlayerMessage setUniqueId(UUID uniqueId) {
        this.uniqueId = uniqueId;
        return this;
    }

    public String getMessage() {
        return message;
    }

    public KickPlayerMessage setMessage(String message) {
        this.message = message;
        return this;
    }

    @Override
    public String toString() {
        return "KickPlayerMessage{" +
                "uniqueId=" + uniqueId +
                ", message='" + message + '\'' +
                '}';
    }
}
