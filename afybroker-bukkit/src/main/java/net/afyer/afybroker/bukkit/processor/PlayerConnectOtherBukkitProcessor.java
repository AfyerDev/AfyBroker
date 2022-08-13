package net.afyer.afybroker.bukkit.processor;

import com.alipay.remoting.AsyncContext;
import com.alipay.remoting.BizContext;
import com.alipay.remoting.rpc.protocol.AsyncUserProcessor;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import lombok.Setter;
import net.afyer.afybroker.bukkit.api.event.AsyncPlayerConnectOtherEvent;
import net.afyer.afybroker.client.BrokerClient;
import net.afyer.afybroker.client.aware.BrokerClientAware;
import net.afyer.afybroker.core.message.PlayerConnectOtherMessage;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

/**
 * @author Nipuru
 * @since 2022/8/3 18:07
 */
public class PlayerConnectOtherBukkitProcessor extends AsyncUserProcessor<PlayerConnectOtherMessage> implements BrokerClientAware {

    @Setter
    BrokerClient brokerClient;

    final Plugin plugin;

    public PlayerConnectOtherBukkitProcessor(Plugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public void handleRequest(BizContext bizCtx, AsyncContext asyncCtx, PlayerConnectOtherMessage request) {
        Player player = Bukkit.getPlayer(request.getPlayer());

        if (player == null) {
            return;
        }

        AsyncPlayerConnectOtherEvent event = new AsyncPlayerConnectOtherEvent(player, request.getServer());

        if (event.callEvent()) {
            ByteArrayDataOutput msg = ByteStreams.newDataOutput();
            msg.writeUTF("Connect");
            msg.writeUTF(event.getServer());
            player.sendPluginMessage(plugin, "BungeeCord", msg.toByteArray());
        }

    }

    @Override
    public String interest() {
        return PlayerConnectOtherMessage.class.getName();
    }
}
