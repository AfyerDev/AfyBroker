package net.afyer.afybroker.core.message;

import java.io.Serializable;
import java.util.UUID;

/**
 * 让玩家连接到某个 minecraft 服务器
 *
 * @author Nipuru
 * @since 2022/9/6 17:33
 */
public class ConnectToServerMessage implements Serializable {
    private static final long serialVersionUID = -2031147618861482881L;

    /** 玩家uuid */
    private UUID uniqueId;

    /** minecraft 服务器名（在 proxy 中的名字） */
    private String serverName;

    public UUID getUniqueId() {
        return uniqueId;
    }

    public ConnectToServerMessage setUniqueId(UUID uniqueId) {
        this.uniqueId = uniqueId;
        return this;
    }

    public String getServerName() {
        return serverName;
    }

    public ConnectToServerMessage setServerName(String serverName) {
        this.serverName = serverName;
        return this;
    }

    @Override
    public String toString() {
        return "ConnectToServerMessage{" +
                "uniqueId=" + uniqueId +
                ", serverName='" + serverName + '\'' +
                '}';
    }
}
