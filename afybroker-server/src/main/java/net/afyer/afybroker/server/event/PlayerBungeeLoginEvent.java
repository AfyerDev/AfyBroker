package net.afyer.afybroker.server.event;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import net.afyer.afybroker.server.plugin.Event;
import net.afyer.afybroker.server.proxy.BrokerPlayer;

/**
 * @author Nipuru
 * @since 2022/8/13 9:20
 */
@Getter
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PlayerBungeeLoginEvent extends Event {

    /** 玩家代理 */
    final BrokerPlayer player;

}
