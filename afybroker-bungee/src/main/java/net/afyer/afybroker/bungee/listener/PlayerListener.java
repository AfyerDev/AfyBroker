package net.afyer.afybroker.bungee.listener;

import net.afyer.afybroker.bungee.AfyBroker;
import net.afyer.afybroker.client.BrokerClient;
import net.afyer.afybroker.core.message.PlayerBungeeConnectMessage;
import net.afyer.afybroker.core.message.PlayerBungeeDisconnectMessage;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.event.LoginEvent;
import net.md_5.bungee.api.event.PlayerDisconnectEvent;
import net.md_5.bungee.event.EventHandler;

/**
 * @author Nipuru
 * @since 2022/7/30 18:44
 */
public class PlayerListener extends AbstractListener {

    private final AfyBroker plugin;

    public PlayerListener(AfyBroker plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onConnect(LoginEvent event) {
        event.registerIntent(plugin);

        ProxyServer.getInstance().getScheduler().runAsync(plugin, () -> {
            try {
                PlayerBungeeConnectMessage msg = new PlayerBungeeConnectMessage()
                        .setUid(event.getConnection().getUniqueId())
                        .setName(event.getConnection().getName());

                boolean result = plugin.getBrokerClient().invokeSync(msg);
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
        BrokerClient brokerClient = plugin.getBrokerClient();

        ProxyServer.getInstance().getScheduler().runAsync(plugin, () -> {
            PlayerBungeeDisconnectMessage msg = new PlayerBungeeDisconnectMessage()
                    .setUid(event.getPlayer().getUniqueId())
                    .setName(event.getPlayer().getName());

            brokerClient.oneway(msg);
        });
    }

}
