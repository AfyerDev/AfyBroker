package net.afyer.afybroker.server.event;

import net.afyer.afybroker.server.plugin.Event;
import net.afyer.afybroker.server.proxy.BrokerPlayer;

import javax.annotation.Nullable;

/**
 * @author Nipuru
 * @since 2022/8/13 9:20
 */
public class PlayerProxyLoginEvent extends Event {

    /**
     * 玩家代理
     */
    private final BrokerPlayer player;

    /**
     * 玩家登录 proxy时 时连接的 bukkit 服务器名
     */
    private @Nullable String serverName;

    public PlayerProxyLoginEvent(BrokerPlayer player, @Nullable String serverName) {
        this.player = player;
        this.serverName = serverName;
    }

    public BrokerPlayer getPlayer() {
        return player;
    }

    public @Nullable String getServerName() {
        return serverName;
    }

    public void setServerName(@Nullable String serverName) {
        this.serverName = serverName;
    }

}
