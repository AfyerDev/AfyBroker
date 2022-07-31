package net.afyer.afybroker.server.proxy;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import net.afyer.afybroker.server.BrokerServer;

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

    final BrokerServer server;

    /** 玩家UUID */
    final UUID uid;
    /** 玩家所在的Bungeecord名称 */
    String bungeeProxy;
    /** 玩家所在的Bukkit名称 */
    String bukkitServer;

    public BrokerPlayer(BrokerServer server, UUID uid) {
        this.server = server;
        this.uid = uid;
    }

    /** 获取玩家所在的 Bungee BrokerClientProxy */
    public BrokerClientProxy getBungeeClientProxy() {
        String bungeeProxy = this.bungeeProxy;
        if (bungeeProxy == null) {
            return null;
        }
        return server.getBrokerClientProxyManager().getByName(bungeeProxy);
    }

    /** 获取玩家所在的 Bukkit BrokerClientProxy */
    public BrokerClientProxy getBukkitClientProxy() {
        String bukkitServer = this.bukkitServer;
        if (bukkitServer == null) {
            return null;
        }
        return server.getBrokerClientProxyManager().getByName(bukkitServer);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BrokerPlayer that = (BrokerPlayer) o;
        return uid.equals(that.uid);
    }

    @Override
    public int hashCode() {
        return Objects.hash(uid);
    }

    @Override
    public String toString() {
        return "BrokerPlayer{" +
                "uid=" + uid +
                ", bungeeProxy='" + bungeeProxy + '\'' +
                ", bukkitServer='" + bukkitServer + '\'' +
                '}';
    }
}
