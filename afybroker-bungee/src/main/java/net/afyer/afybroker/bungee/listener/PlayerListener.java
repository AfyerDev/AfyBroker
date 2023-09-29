package net.afyer.afybroker.bungee.listener;

import com.alipay.remoting.exception.RemotingException;
import com.google.common.collect.Maps;
import net.afyer.afybroker.bungee.AfyBroker;
import net.afyer.afybroker.client.BrokerClient;
import net.afyer.afybroker.core.message.PlayerBukkitConnectedMessage;
import net.afyer.afybroker.core.message.PlayerBungeeConnectMessage;
import net.afyer.afybroker.core.message.PlayerBungeeDisconnectMessage;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.PendingConnection;
import net.md_5.bungee.api.event.LoginEvent;
import net.md_5.bungee.api.event.PlayerDisconnectEvent;
import net.md_5.bungee.api.event.PostLoginEvent;
import net.md_5.bungee.api.event.ServerConnectedEvent;
import net.md_5.bungee.api.scheduler.ScheduledTask;
import net.md_5.bungee.event.EventHandler;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * @author Nipuru
 * @since 2022/7/30 18:44
 */
public class PlayerListener extends AbstractListener {

    private final AfyBroker plugin;
    private final Map<UUID, ScheduledTask> disconnectTasks = Maps.newConcurrentMap();

    public PlayerListener(AfyBroker plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = (byte) 128)
    public void onConnectNormal(LoginEvent event) {
        event.registerIntent(plugin);

        PendingConnection connection = event.getConnection();
        UUID uniqueId = connection.getUniqueId();
        ScheduledTask t = disconnectTasks.remove(uniqueId);
        if (t != null) {
            t.cancel();
        }
        ProxyServer.getInstance().getScheduler().runAsync(plugin, () -> {
            try {
                PlayerBungeeConnectMessage connectMessage = new PlayerBungeeConnectMessage()
                        .setUid(connection.getUniqueId())
                        .setName(connection.getName());

                boolean result = plugin.getBrokerClient().invokeSync(connectMessage);

                if (!result) {
                    event.setCancelled(true);
                    event.setCancelReason(new TextComponent("§c登录失败"));
                } else {
                    ScheduledTask task = ProxyServer.getInstance().getScheduler().schedule(plugin, () -> {
                        if (disconnectTasks.remove(connection.getUniqueId()) == null) return;
                        PlayerBungeeDisconnectMessage disconnectMessage = new PlayerBungeeDisconnectMessage()
                                .setUid(connection.getUniqueId())
                                .setName(connection.getName());
                        try {
                            plugin.getBrokerClient().oneway(disconnectMessage);
                        } catch (RemotingException | InterruptedException e) {
                            e.printStackTrace();
                        }
                    }, 5L, TimeUnit.SECONDS);
                    disconnectTasks.put(connection.getUniqueId(), task);
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
    public void postConnect(PostLoginEvent event) {
        UUID uniqueId = event.getPlayer().getUniqueId();
        ScheduledTask task = disconnectTasks.remove(uniqueId);
        if (task != null) {
            task.cancel();
        } else {
            event.getPlayer().disconnect(new TextComponent("§c产生了一个错误"));
        }
    }

    @EventHandler
    public void onDisConnect(PlayerDisconnectEvent event) {
        ProxyServer.getInstance().getScheduler().runAsync(plugin, () -> {
            PlayerBungeeDisconnectMessage msg = new PlayerBungeeDisconnectMessage()
                    .setUid(event.getPlayer().getUniqueId())
                    .setName(event.getPlayer().getName());

            try {
                plugin.getBrokerClient().oneway(msg);
            } catch (RemotingException | InterruptedException e) {
                e.printStackTrace();
            }
        });
    }

    @EventHandler
    public void onServerConnected(ServerConnectedEvent event) {
        BrokerClient brokerClient = plugin.getBrokerClient();

        ProxyServer.getInstance().getScheduler().runAsync(plugin, () -> {
            PlayerBukkitConnectedMessage msg = new PlayerBukkitConnectedMessage()
                    .setPlayerName(event.getPlayer().getName())
                    .setPlayerUniqueId(event.getPlayer().getUniqueId())
                    .setServerName(event.getServer().getInfo().getName());

            try {
                brokerClient.oneway(msg);
            } catch (RemotingException | InterruptedException e) {
                e.printStackTrace();
            }
        });
    }

}
