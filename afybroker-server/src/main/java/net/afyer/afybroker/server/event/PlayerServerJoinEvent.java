package net.afyer.afybroker.server.event;

import net.afyer.afybroker.server.plugin.Event;
import net.afyer.afybroker.server.proxy.BrokerClientItem;
import net.afyer.afybroker.server.proxy.BrokerPlayer;
import org.jetbrains.annotations.Nullable;

/**
 * @author Nipuru
 * @since 2023/09/29 12:18
 */
public class PlayerServerJoinEvent extends Event {

    /** 玩家代理 */
    private final BrokerPlayer player;

    /** 玩家之前所在的 server 代理 */
    @Nullable
    private final BrokerClientItem previous;

    /** 玩家当前所在的 server 代理 */
    private final BrokerClientItem current;

    public PlayerServerJoinEvent(BrokerPlayer player, @Nullable BrokerClientItem previous, BrokerClientItem current) {
        this.player = player;
        this.previous = previous;
        this.current = current;
    }

    public BrokerPlayer getPlayer() {
        return player;
    }

    @Nullable
    public BrokerClientItem getPrevious() {
        return previous;
    }

    public BrokerClientItem getCurrent() {
        return current;
    }

}
