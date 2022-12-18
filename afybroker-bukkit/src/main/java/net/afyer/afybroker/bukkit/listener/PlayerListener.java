package net.afyer.afybroker.bukkit.listener;

import com.alipay.remoting.exception.RemotingException;
import net.afyer.afybroker.bukkit.AfyBroker;
import net.afyer.afybroker.client.BrokerClient;
import net.afyer.afybroker.core.message.PlayerBukkitJoinMessage;
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

        plugin.getServer().getScheduler().runTaskAsynchronously(plugin, () -> {
            PlayerBukkitJoinMessage msg = new PlayerBukkitJoinMessage()
                    .setUid(player.getUniqueId())
                    .setName(player.getName());

            try {
                brokerClient.oneway(msg);
            } catch (RemotingException | InterruptedException e) {
                e.printStackTrace();
                player.kick(null);
            }
        });
    }

}
