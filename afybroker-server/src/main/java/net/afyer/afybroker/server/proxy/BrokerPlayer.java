package net.afyer.afybroker.server.proxy;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import net.afyer.afybroker.core.message.ConnectToServerMessage;
import net.afyer.afybroker.core.message.KickPlayerMessage;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.UUID;

/**
 * 玩家代理
 *
 * @author Nipuru
 * @since 2022/7/30 16:47
 */
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class BrokerPlayer {

    /** 玩家UUID */
    final UUID uniqueId;
    /** 玩家名字 */
    final String name;
    /** 玩家所在的 Proxy 服务器客户端代理 */
    final BrokerClientItem proxy;

    /** 玩家所在的 Minecraft 服务器客户端代理 */
    @Nullable
    BrokerClientItem server;

    public BrokerPlayer(UUID uniqueId, String name, BrokerClientItem proxy) {
        this.uniqueId = uniqueId;
        this.name = name;
        this.proxy = proxy;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BrokerPlayer that = (BrokerPlayer) o;
        return uniqueId.equals(that.uniqueId);
    }

    public void kick(String message) throws Exception {
        KickPlayerMessage request = new KickPlayerMessage()
                .setUniqueId(uniqueId)
                .setMessage(message);

        proxy.oneway(request);
    }

    public void connectToServer(String serverName) throws Exception {
        ConnectToServerMessage request = new ConnectToServerMessage()
                .setUniqueId(uniqueId)
                .setServerName(serverName);

        proxy.oneway(request);
    }

    @Override
    public int hashCode() {
        return Objects.hash(uniqueId);
    }

    @Override
    public String toString() {
        return "BrokerPlayer{" +
                ", uid=" + uniqueId +
                ", name='" + name + '\'' +
                ", bungeeProxy='" + proxy + '\'' +
                ", bukkitServer='" + proxy + '\'' +
                '}';
    }
}
