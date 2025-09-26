package net.afyer.afybroker.server.event;

import net.afyer.afybroker.server.plugin.Event;
import net.afyer.afybroker.server.proxy.BrokerPlayer;

/**
 * @author Nipuru
 * @since 2022/12/4 21:15
 */
public class PlayerServerConnectedEvent extends Event {

    /** 玩家代理 */
    private final BrokerPlayer player;

    public PlayerServerConnectedEvent(BrokerPlayer player) {
        this.player = player;
    }

    public BrokerPlayer getPlayer() {
        return player;
    }

}
