package net.afyer.afybroker.server.event;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import net.afyer.afybroker.server.plugin.Event;
import net.afyer.afybroker.server.proxy.BrokerClientProxy;
import net.afyer.afybroker.server.proxy.BrokerPlayer;
import org.jetbrains.annotations.Nullable;

/**
 * @author Nipuru
 * @since 2022/12/4 21:15
 */
@Getter
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PlayerBukkitConnectedEvent extends Event {

    /** 玩家代理 */
    final BrokerPlayer player;

    /** 玩家之前所在的 bukkit 代理 */
    @Nullable
    final BrokerClientProxy previous;

    /** 玩家当前所在的 bukkit 代理 */
    final BrokerClientProxy current;

}
