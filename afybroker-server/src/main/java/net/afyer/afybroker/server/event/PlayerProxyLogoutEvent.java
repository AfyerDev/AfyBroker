package net.afyer.afybroker.server.event;

import net.afyer.afybroker.server.plugin.Event;
import net.afyer.afybroker.server.proxy.BrokerPlayer;

/**
 * @author Nipuru
 * @since 2022/8/13 9:20
 */
public class PlayerProxyLogoutEvent extends Event {

    /** 玩家代理 */
    private final BrokerPlayer player;

    public PlayerProxyLogoutEvent(BrokerPlayer player) {
        this.player = player;
    }

    public BrokerPlayer getPlayer() {
        return player;
    }

}
