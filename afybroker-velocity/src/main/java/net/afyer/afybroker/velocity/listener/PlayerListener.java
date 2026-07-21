package net.afyer.afybroker.velocity.listener;

import com.alipay.remoting.exception.RemotingException;
import com.velocitypowered.api.event.EventTask;
import com.velocitypowered.api.event.PostOrder;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.connection.DisconnectEvent;
import com.velocitypowered.api.event.player.PlayerChooseInitialServerEvent;
import com.velocitypowered.api.event.player.ServerConnectedEvent;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.server.RegisteredServer;
import net.afyer.afybroker.core.message.PlayerProxyConnectMessage;
import net.afyer.afybroker.core.message.PlayerProxyConnectResult;
import net.afyer.afybroker.core.message.PlayerProxyDisconnectMessage;
import net.afyer.afybroker.core.message.PlayerServerConnectedMessage;
import net.afyer.afybroker.core.observability.PlayerEventType;
import net.afyer.afybroker.core.observability.PlayerObservation;
import net.afyer.afybroker.velocity.AfyBroker;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;

import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Nipuru
 * @since 2022/7/30 18:44
 */
public class PlayerListener {

    private final AfyBroker plugin;
    private final Set<UUID> acceptedPlayers = ConcurrentHashMap.newKeySet();

    public PlayerListener(AfyBroker plugin) {
        this.plugin = plugin;
    }

    @Subscribe(order = PostOrder.LAST)
    public EventTask onConnect(PlayerChooseInitialServerEvent event) {
        return EventTask.async(() -> handleInitialServer(event));
    }

    private void handleInitialServer(PlayerChooseInitialServerEvent event) {
        Player player = event.getPlayer();
        String initialServerName = event.getInitialServer()
                .map(server -> server.getServerInfo().getName())
                .orElse(null);
        PlayerProxyConnectMessage connectMessage = new PlayerProxyConnectMessage()
                .setUniqueId(player.getUniqueId())
                .setName(player.getUsername())
                .setServerName(initialServerName);

        try {
            PlayerProxyConnectResult result = plugin.getBrokerClient().invokeSync(connectMessage);
            if (!result.isSuccess()) {
                disconnect(player, "Login failed");
                return;
            }

            acceptedPlayers.add(player.getUniqueId());
            plugin.getBrokerClient().getObservability().onPlayer(new PlayerObservation(
                    PlayerEventType.JOIN, plugin.getServer().getPlayerCount()));

            String serverName = result.getServerName();
            RegisteredServer target = serverName == null || serverName.trim().isEmpty()
                    ? null : plugin.getServer().getServer(serverName).orElse(null);
            if (target == null) {
                disconnect(player, "Login failed");
                return;
            }
            event.setInitialServer(target);
        } catch (Exception e) {
            plugin.getLogger().error(e.getMessage(), e);
            disconnect(player, "An error occurred");
        }
    }

    @Subscribe
    public void onDisConnect(DisconnectEvent event) {
        if (!acceptedPlayers.remove(event.getPlayer().getUniqueId())) {
            return;
        }
        plugin.getBrokerClient().getObservability().onPlayer(new PlayerObservation(PlayerEventType.LEAVE, plugin.getServer().getPlayerCount()));
        PlayerProxyDisconnectMessage msg = new PlayerProxyDisconnectMessage()
                .setUniqueId(event.getPlayer().getUniqueId())
                .setName(event.getPlayer().getUsername());

        try {
            plugin.getBrokerClient().oneway(msg);
        } catch (RemotingException | InterruptedException e) {
            plugin.getLogger().error(e.getMessage(), e);
        }
    }

    @Subscribe
    public void onServerConnected(ServerConnectedEvent event) {
        PlayerServerConnectedMessage msg = new PlayerServerConnectedMessage()
                .setName(event.getPlayer().getUsername())
                .setUniqueId(event.getPlayer().getUniqueId())
                .setServerName(event.getServer().getServerInfo().getName());

        try {
            plugin.getBrokerClient().oneway(msg);
        } catch (RemotingException | InterruptedException e) {
            plugin.getLogger().error(e.getMessage(), e);
        }
    }

    private void disconnect(Player player, String message) {
        player.disconnect(Component.text(message).color(NamedTextColor.RED));
    }
}
