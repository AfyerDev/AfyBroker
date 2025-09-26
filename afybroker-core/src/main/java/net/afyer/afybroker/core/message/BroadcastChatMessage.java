package net.afyer.afybroker.core.message;

import java.io.Serializable;

/**
 * @author Nipuru
 * @since 2022/8/10 11:23
 */
public class BroadcastChatMessage implements Serializable {
    private static final long serialVersionUID = -4901406795508836396L;

    /**
     * 消息
     */
    private String message;


    public BroadcastChatMessage setMessage(String message) {
        this.message = message;
        return this;
    }

    public String getMessage() {
        return message;
    }

    @Override
    public String toString() {
        return "BroadcastChatMessage{" +
                "message='" + message + '\'' +
                '}';
    }
}
