package net.afyer.afybroker.server.proxy;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.jetbrains.annotations.Nullable;

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
    final Map<String, BrokerPlayer> byName = new ConcurrentHashMap<>();
    final Map<UUID, BrokerPlayer> view = Collections.unmodifiableMap(byUid);

    public Collection<BrokerPlayer> getPlayers() {
        return view.values();
    }

    @Nullable
    public BrokerPlayer addPlayer(BrokerPlayer player) {
        UUID uid = player.getUniqueId();
        BrokerPlayer absent = byUid.putIfAbsent(uid, player);
        if (absent == null) {
            byName.put(player.getName(), player);
        }
        return absent;
    }

    public void removePlayer(UUID uid) {
        BrokerPlayer player = byUid.remove(uid);
        if (player != null) {
            byName.remove(player.getName());
        }
    }

    @Nullable
    public BrokerPlayer getPlayer(UUID uid) {
        return byUid.get(uid);
    }

    @Nullable
    public BrokerPlayer getPlayer(String name) {
        return byName.get(name);
    }

}
