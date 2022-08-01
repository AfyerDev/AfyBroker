package net.afyer.afybroker.server.proxy;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 玩家代理 管理器
 *
 * @author Nipuru
 * @since 2022/7/30 20:36
 */
@FieldDefaults(level = AccessLevel.PRIVATE)
public class BrokerPlayerManager {

    final Map<UUID, BrokerPlayer> byUid = new ConcurrentHashMap<>();
    final Map<UUID, BrokerPlayer> view = Collections.unmodifiableMap(byUid);

    public Map<UUID, BrokerPlayer> getPlayerMap() {
        return view;
    }

    public Collection<BrokerPlayer> getPlayers() {
        return view.values();
    }

    public BrokerPlayer addPlayer(BrokerPlayer player) {
        UUID uid = player.getUid();

        return byUid.putIfAbsent(uid, player);
    }

    public void removePlayer(UUID uid) {
        byUid.remove(uid);
    }

    public BrokerPlayer getPlayer(UUID uid) {
        return byUid.get(uid);
    }

}
