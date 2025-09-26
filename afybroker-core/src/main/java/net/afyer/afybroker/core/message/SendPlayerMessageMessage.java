package net.afyer.afybroker.core.message;

import java.io.Serializable;
import java.util.UUID;

/**
 * @author Nipuru
 * @since 2022/8/5 10:04
 */
public class SendPlayerMessageMessage implements Serializable {
    private static final long serialVersionUID = -4207075992906096144L;

    /**
     * 玩家 uniqueId
     */
    private UUID uniqueId;

    /**
     * 消息
     */
    private String message;

    public UUID getUniqueId() {
        return uniqueId;
    }

    public SendPlayerMessageMessage setUniqueId(UUID uniqueId) {
        this.uniqueId = uniqueId;
        return this;
    }

    public String getMessage() {
        return message;
    }

    public SendPlayerMessageMessage setMessage(String message) {
        this.message = message;
        return this;
    }

    @Override
    public String toString() {
        return "SendPlayerMessageMessage{" +
                "uniqueId=" + uniqueId +
                ", message='" + message + '\'' +
                '}';
    }
}
