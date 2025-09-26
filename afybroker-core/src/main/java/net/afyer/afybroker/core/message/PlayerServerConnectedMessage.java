package net.afyer.afybroker.core.message;

import java.io.Serializable;
import java.util.UUID;

/**
 * 玩家与 minecraft 服务器连接状态变化的消息
 * @author Nipuru
 * @since 2022/9/12 12:21
 */
public class PlayerServerConnectedMessage implements Serializable {
    private static final long serialVersionUID = 5436035428469761938L;

    /** 玩家uuid */
    private UUID uniqueId;

    /** 玩家名 */
    private String name;

    /** 服务器名 */
    private String serverName;

    public UUID getUniqueId() {
        return uniqueId;
    }

    public PlayerServerConnectedMessage setUniqueId(UUID uniqueId) {
        this.uniqueId = uniqueId;
        return this;
    }

    public String getName() {
        return name;
    }

    public PlayerServerConnectedMessage setName(String name) {
        this.name = name;
        return this;
    }

    public String getServerName() {
        return serverName;
    }

    public PlayerServerConnectedMessage setServerName(String serverName) {
        this.serverName = serverName;
        return this;
    }

    @Override
    public String toString() {
        return "PlayerServerConnectedMessage{" +
                "uniqueId=" + uniqueId +
                ", name='" + name + '\'' +
                ", serverName='" + serverName + '\'' +
                '}';
    }
}
