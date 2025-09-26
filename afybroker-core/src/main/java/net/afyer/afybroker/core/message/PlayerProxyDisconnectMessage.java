package net.afyer.afybroker.core.message;

import java.io.Serializable;
import java.util.UUID;

/**
 * 玩家从 proxy 断开连接的消息
 * @author Nipuru
 * @since 2022/11/21 17:30
 */
public class PlayerProxyDisconnectMessage implements Serializable {
    private static final long serialVersionUID = -5160344925177364814L;

    /** 玩家uuid */
    private UUID uniqueId;

    /** 玩家名 */
    private String name;

    public UUID getUniqueId() {
        return uniqueId;
    }

    public PlayerProxyDisconnectMessage setUniqueId(UUID uniqueId) {
        this.uniqueId = uniqueId;
        return this;
    }

    public String getName() {
        return name;
    }

    public PlayerProxyDisconnectMessage setName(String name) {
        this.name = name;
        return this;
    }

    @Override
    public String toString() {
        return "PlayerProxyDisconnectMessage{" +
                "uniqueId=" + uniqueId +
                ", name='" + name + '\'' +
                '}';
    }
}
