package net.afyer.afybroker.bukkit.listener;

import com.alipay.remoting.exception.RemotingException;
import net.afyer.afybroker.bukkit.AfyBroker;
import net.afyer.afybroker.core.message.PlayerServerJoinMessage;
import net.afyer.afybroker.core.observability.PlayerEventType;
import net.afyer.afybroker.core.observability.PlayerObservation;
import net.afyer.afybroker.bukkit.util.Schedulers;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

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
        plugin.getBrokerClient().getObservability().onPlayer(new PlayerObservation(PlayerEventType.JOIN, Bukkit.getOnlinePlayers().size()));
        Player player = event.getPlayer();
        PlayerServerJoinMessage message = new PlayerServerJoinMessage()
                .setName(player.getName())
                .setUniqueId(player.getUniqueId());
        Schedulers.runAsync(plugin, () -> {
            try {
                plugin.getBrokerClient().oneway(message);
            } catch (RemotingException | InterruptedException e) {
                plugin.getLogger().log(Level.SEVERE, e.getMessage(), e);
                Schedulers.runForEntity(plugin, player, () -> player.kickPlayer(null));
            }
        });
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onQuit(PlayerQuitEvent event) {
        plugin.getBrokerClient().getObservability().onPlayer(new PlayerObservation(PlayerEventType.LEAVE, Bukkit.getOnlinePlayers().size()));
    }
}
