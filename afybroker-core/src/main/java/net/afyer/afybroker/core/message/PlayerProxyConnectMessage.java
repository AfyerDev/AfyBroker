package net.afyer.afybroker.core.message;


import java.io.Serializable;
import java.util.UUID;

/**
 * 玩家连接至 proxy 的消息
 *
 * @author Nipuru
 * @since 2022/8/1 11:32
 */
public class PlayerProxyConnectMessage implements Serializable {
    private static final long serialVersionUID = 1791475059445212432L;

    /**
     * 玩家uuid
     */
    private UUID uniqueId;

    /**
     * 玩家名
     */
    private String name;

    /**
     * 初始连接的 bukkit 服务器名
     */
    private String serverName;

    public UUID getUniqueId() {
        return uniqueId;
    }

    public PlayerProxyConnectMessage setUniqueId(UUID uniqueId) {
        this.uniqueId = uniqueId;
        return this;
    }

    public String getName() {
        return name;
    }

    public PlayerProxyConnectMessage setName(String name) {
        this.name = name;
        return this;
    }

    public String getServerName() {
        return serverName;
    }

    public PlayerProxyConnectMessage setServerName(String serverName) {
        this.serverName = serverName;
        return this;
    }

    @Override
    public String toString() {
        return "PlayerProxyConnectMessage{" +
                "uniqueId=" + uniqueId +
                ", name='" + name + '\'' +
                ", serverName='" + serverName + '\'' +
                '}';
    }
}
