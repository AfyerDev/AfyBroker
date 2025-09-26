package net.afyer.afybroker.velocity.processor;


import com.alipay.remoting.BizContext;
import com.alipay.remoting.rpc.protocol.SyncUserProcessor;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.util.GameProfile;
import net.afyer.afybroker.core.message.PlayerProfilePropertyMessage;
import net.afyer.afybroker.velocity.AfyBroker;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Nipuru
 * @since 2024/12/03 10:20
 */
public class PlayerProfilePropertyVelocityProcessor extends SyncUserProcessor<PlayerProfilePropertyMessage> {

    private final AfyBroker plugin;

    public PlayerProfilePropertyVelocityProcessor(AfyBroker plugin) {
        this.plugin = plugin;
    }

    @Override
    public Object handleRequest(BizContext bizCtx, PlayerProfilePropertyMessage request) throws Exception {
        Player player = plugin.getServer().getPlayer(request.getUniqueId()).orElse(null);
        if (player == null) {
            return false;
        }
        Map<String, GameProfile.Property> properties = new HashMap<>();
        for (GameProfile.Property property : player.getGameProfileProperties()) {
            properties.put(property.getName(), property);
        }
        if (request.getRemoveList() != null) {
            for (String name : request.getRemoveList()) {
                properties.remove(name);
            }
        }
        if (request.getUpdateMap() != null) {
            for (Map.Entry<String, String[]> entry : request.getUpdateMap().entrySet()) {
                String name = entry.getKey();
                String value = entry.getValue()[0];
                String signature = entry.getValue()[1];
                properties.put(name, new GameProfile.Property(name, value, signature));
            }
        }
        player.setGameProfileProperties(new ArrayList<>(properties.values()));
        return true;
    }

    @Override
    public String interest() {
        return PlayerProfilePropertyMessage.class.getName();
    }
}
