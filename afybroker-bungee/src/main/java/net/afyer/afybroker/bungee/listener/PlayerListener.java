package net.afyer.afybroker.bungee.listener;

import com.alipay.remoting.exception.RemotingException;
import net.afyer.afybroker.bungee.AfyBroker;
import net.afyer.afybroker.client.BrokerClient;
import net.afyer.afybroker.core.message.PlayerProxyConnectMessage;
import net.afyer.afybroker.core.message.PlayerProxyConnectResult;
import net.afyer.afybroker.core.message.PlayerProxyDisconnectMessage;
import net.afyer.afybroker.core.message.PlayerServerConnectedMessage;
import net.afyer.afybroker.core.observability.PlayerEventType;
import net.afyer.afybroker.core.observability.PlayerObservation;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.PlayerDisconnectEvent;
import net.md_5.bungee.api.event.ServerConnectEvent;
import net.md_5.bungee.api.event.ServerConnectedEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;

/**
 * @author Nipuru
 * @since 2022/7/30 18:44
 */
public class PlayerListener implements Listener {

    private final AfyBroker plugin;
    private final Set<UUID> acceptedPlayers = ConcurrentHashMap.newKeySet();

    public PlayerListener(AfyBroker plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = Byte.MAX_VALUE)
    public void onConnect(ServerConnectEvent event) {
        if (event.getReason() != ServerConnectEvent.Reason.JOIN_PROXY) {
            return;
        }

        ProxiedPlayer player = event.getPlayer();
        ServerInfo initialServer = event.getTarget();
        PlayerProxyConnectMessage connectMessage = new PlayerProxyConnectMessage()
                .setUniqueId(player.getUniqueId())
                .setName(player.getName())
                .setServerName(initialServer.getName());

        try {
            PlayerProxyConnectResult result = plugin.getBrokerClient().invokeSync(connectMessage);
            if (!result.isSuccess()) {
                disconnect(event, "Login failed");
                return;
            }

            acceptedPlayers.add(player.getUniqueId());
            plugin.getBrokerClient().getObservability().onPlayer(new PlayerObservation(
                    PlayerEventType.JOIN, ProxyServer.getInstance().getOnlineCount()));

            String serverName = result.getServerName();
            ServerInfo target = serverName == null || serverName.trim().isEmpty()
                    ? null : ProxyServer.getInstance().getServerInfo(serverName);
            if (target == null) {
                disconnect(event, "Login failed");
                return;
            }
            event.setTarget(target);
        } catch (Exception ex) {
            plugin.getLogger().log(Level.SEVERE, ex.getMessage(), ex);
            disconnect(event, "An error occurred");
        }
    }

    @EventHandler
    public void onDisConnect(PlayerDisconnectEvent event) {
        if (!acceptedPlayers.remove(event.getPlayer().getUniqueId())) {
            return;
        }
        plugin.getBrokerClient().getObservability().onPlayer(new PlayerObservation(PlayerEventType.LEAVE, ProxyServer.getInstance().getOnlineCount()));
        ProxyServer.getInstance().getScheduler().runAsync(plugin, () -> {
            PlayerProxyDisconnectMessage msg = new PlayerProxyDisconnectMessage()
                    .setUniqueId(event.getPlayer().getUniqueId())
                    .setName(event.getPlayer().getName());

            try {
                plugin.getBrokerClient().oneway(msg);
            } catch (RemotingException | InterruptedException e) {
                plugin.getLogger().log(Level.SEVERE, e.getMessage(), e);
            }
        });
    }

    @EventHandler
    public void onServerConnected(ServerConnectedEvent event) {
        BrokerClient brokerClient = plugin.getBrokerClient();

        ProxyServer.getInstance().getScheduler().runAsync(plugin, () -> {
            PlayerServerConnectedMessage msg = new PlayerServerConnectedMessage()
                    .setName(event.getPlayer().getName())
                    .setUniqueId(event.getPlayer().getUniqueId())
                    .setServerName(event.getServer().getInfo().getName());

            try {
                brokerClient.oneway(msg);
            } catch (RemotingException | InterruptedException e) {
                plugin.getLogger().log(Level.SEVERE, e.getMessage(), e);
            }
        });
    }

    private void disconnect(ServerConnectEvent event, String message) {
        event.setCancelled(true);
        event.getPlayer().disconnect(new TextComponent(message));
    }
}
