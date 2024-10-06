package net.afyer.afybroker.server.event;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import net.afyer.afybroker.server.plugin.Event;
import net.afyer.afybroker.server.proxy.BrokerClientItem;
import net.afyer.afybroker.server.proxy.BrokerPlayer;
import org.jetbrains.annotations.Nullable;

/**
 * @author Nipuru
 * @since 2023/09/29 12:18
 */
@Getter
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PlayerServerJoinEvent extends Event {

    /** 玩家代理 */
    final BrokerPlayer player;

    /** 玩家之前所在的 server 代理 */
    @Nullable
    final BrokerClientItem previous;

    /** 玩家当前所在的 server 代理 */
    final BrokerClientItem current;

}
