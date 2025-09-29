package net.afyer.afybroker.bukkit.listener;

import com.alipay.remoting.exception.RemotingException;
import net.afyer.afybroker.bukkit.AfyBroker;
import net.afyer.afybroker.core.message.PlayerServerJoinMessage;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import java.util.logging.Level;

/**
 * @author Nipuru
 * @since 2023/09/29 12:05
 */
public class PlayerListener implements Listener {

    private final AfyBroker plugin;

    public PlayerListener(AfyBroker plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onJoin(PlayerJoinEvent event) {
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            PlayerServerJoinMessage message = new PlayerServerJoinMessage()
                    .setName(event.getPlayer().getName())
                    .setUniqueId(event.getPlayer().getUniqueId());
            try {
                plugin.getBrokerClient().oneway(message);
            } catch (RemotingException | InterruptedException e) {
                plugin.getLogger().log(Level.SEVERE, e.getMessage(), e);
                Bukkit.getScheduler().runTask(plugin, () -> event.getPlayer().kickPlayer(null));
            }
        });
    }
}
