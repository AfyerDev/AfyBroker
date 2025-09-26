package net.afyer.afybroker.core.message;

import java.io.Serializable;
import java.util.UUID;

/**
 * @author Nipuru
 * @since 2023/09/29 12:04
 */
public class PlayerServerJoinMessage implements Serializable {
    private static final long serialVersionUID = -1132388839270494188L;

    /** 玩家uuid */
    private UUID uniqueId;

    /** 玩家名 */
    private String name;

    public UUID getUniqueId() {
        return uniqueId;
    }

    public PlayerServerJoinMessage setUniqueId(UUID uniqueId) {
        this.uniqueId = uniqueId;
        return this;
    }

    public String getName() {
        return name;
    }

    public PlayerServerJoinMessage setName(String name) {
        this.name = name;
        return this;
    }

    @Override
    public String toString() {
        return "PlayerServerJoinMessage{" +
                "uniqueId=" + uniqueId +
                ", name='" + name + '\'' +
                '}';
    }
}
