package net.afyer.afybroker.bungee.listener;

import com.alipay.remoting.exception.RemotingException;
import net.afyer.afybroker.bungee.AfyBroker;
import net.afyer.afybroker.client.BrokerClient;
import net.afyer.afybroker.core.message.PlayerProxyConnectMessage;
import net.afyer.afybroker.core.message.PlayerProxyDisconnectMessage;
import net.afyer.afybroker.core.message.PlayerServerConnectedMessage;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.PendingConnection;
import net.md_5.bungee.api.event.LoginEvent;
import net.md_5.bungee.api.event.PlayerDisconnectEvent;
import net.md_5.bungee.api.event.ServerConnectedEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

import java.util.logging.Level;

/**
 * @author Nipuru
 * @since 2022/7/30 18:44
 */
public class PlayerListener implements Listener {

    private final AfyBroker plugin;

    public PlayerListener(AfyBroker plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = (byte) 128)
    public void onConnect(LoginEvent event) {
        event.registerIntent(plugin);

        PendingConnection connection = event.getConnection();
        ProxyServer.getInstance().getScheduler().runAsync(plugin, () -> {
            try {
                PlayerProxyConnectMessage connectMessage = new PlayerProxyConnectMessage()
                        .setUniqueId(connection.getUniqueId())
                        .setName(connection.getName());

                boolean result = plugin.getBrokerClient().invokeSync(connectMessage);

                if (!result) {
                    event.setCancelled(true);
                    event.setCancelReason(new TextComponent("§c登录失败"));
                }
            } catch (Exception ex) {
                event.setCancelled(true);
                event.setCancelReason(new TextComponent("§c产生了一个错误"));
            } finally {
                event.completeIntent(plugin);
            }
        });
    }

    @EventHandler
    public void onDisConnect(PlayerDisconnectEvent event) {
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

}
