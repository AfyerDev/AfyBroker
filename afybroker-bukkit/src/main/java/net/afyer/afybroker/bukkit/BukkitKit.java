package net.afyer.afybroker.bukkit;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import lombok.experimental.UtilityClass;
import net.afyer.afybroker.bukkit.api.event.AsyncPlayerConnectOtherEvent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.UUID;

/**
 * @author Nipuru
 * @since 2022/8/3 18:27
 */
@SuppressWarnings("UnstableApiUsage")
@UtilityClass
public class BukkitKit {

    public void playerConnectOther(String playerName, String server) {
        Player player = Bukkit.getPlayer(playerName);
        if (player == null) {
            return;
        }
        playerConnectOther(player, server);
    }

    public void playerConnectOther(UUID playerUid, String server) {
        Player player = Bukkit.getPlayer(playerUid);
        if (player == null) {
            return;
        }
        playerConnectOther(player, server);
    }

    public void playerConnectOther(Player player, String server) {
        AfyBroker plugin = AfyBroker.getInstance();
        if (Bukkit.isPrimaryThread()) {
            plugin.getServer().getScheduler().runTaskAsynchronously(plugin, () -> playerConnectOther(player, server));
            return;
        }

        AsyncPlayerConnectOtherEvent event = new AsyncPlayerConnectOtherEvent(player, server);

        if (event.callEvent()) {
            ByteArrayDataOutput msg = ByteStreams.newDataOutput();
            msg.writeUTF("Connect");
            msg.writeUTF(server);
            player.sendPluginMessage(plugin, "BungeeCord", msg.toByteArray());
        }
    }
}
