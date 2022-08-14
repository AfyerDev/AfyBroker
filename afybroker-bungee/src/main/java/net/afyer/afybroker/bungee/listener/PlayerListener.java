package net.afyer.afybroker.bungee.listener;

import net.afyer.afybroker.bungee.AfyBroker;
import net.afyer.afybroker.client.BrokerClient;
import net.afyer.afybroker.core.message.BrokerClientInfoMessage;
import net.afyer.afybroker.core.message.PlayerBungeeMessage;
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
        BrokerClient brokerClient = plugin.getBrokerClient();
        BrokerClientInfoMessage clientInfo = brokerClient.getClientInfo();

        ProxyServer.getInstance().getScheduler().runAsync(plugin, () -> {
            try {
                PlayerBungeeMessage msg = new PlayerBungeeMessage()
                        .setClientName(clientInfo.getName())
                        .setUid(event.getConnection().getUniqueId())
                        .setName(event.getConnection().getName())
                        .setState(PlayerBungeeMessage.State.CONNECT);

                boolean result = plugin.getBrokerClient().invokeSync(msg);
                if (!result) {
                    event.setCancelled(true);
                    event.setCancelReason(new TextComponent("§c此账号已登录"));
                }
            } finally {
                event.completeIntent(plugin);
            }
        });
    }

    @EventHandler
    public void onDisConnect(PlayerDisconnectEvent event) {
        BrokerClient brokerClient = plugin.getBrokerClient();
        BrokerClientInfoMessage clientInfo = brokerClient.getClientInfo();

        ProxyServer.getInstance().getScheduler().runAsync(plugin, () -> {
            PlayerBungeeMessage msg = new PlayerBungeeMessage()
                    .setClientName(clientInfo.getName())
                    .setUid(event.getPlayer().getUniqueId())
                    .setName(event.getPlayer().getName())
                    .setState(PlayerBungeeMessage.State.DISCONNECT);

            brokerClient.oneway(msg);
        });
    }

}
