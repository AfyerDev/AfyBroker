package net.afyer.afybroker.bukkit.listener;

import net.afyer.afybroker.bukkit.AfyBroker;
import net.afyer.afybroker.client.BrokerClient;
import net.afyer.afybroker.core.message.BrokerClientInfoMessage;
import net.afyer.afybroker.core.message.PlayerBungeeMessage;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerJoinEvent;

/**
 * @author Nipuru
 * @since 2022/8/1 15:52
 */
public class PlayerListener extends AbstractListener {

    private final AfyBroker plugin;

    public PlayerListener(AfyBroker plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {

        Player player = event.getPlayer();
        BrokerClient brokerClient = plugin.getBrokerClient();
        BrokerClientInfoMessage clientInfo = brokerClient.getClientInfo();

        plugin.getServer().getScheduler().runTaskAsynchronously(plugin, () -> {
            PlayerBungeeMessage msg = new PlayerBungeeMessage()
                    .setClientName(clientInfo.getName())
                    .setUid(player.getUniqueId())
                    .setName(player.getName())
                    .setState(PlayerBungeeMessage.State.JOIN);

            brokerClient.oneway(msg);
        });
    }

}
