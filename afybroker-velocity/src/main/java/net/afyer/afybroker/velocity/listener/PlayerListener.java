package net.afyer.afybroker.velocity.listener;

import com.alipay.remoting.exception.RemotingException;
import com.velocitypowered.api.event.ResultedEvent;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.connection.DisconnectEvent;
import com.velocitypowered.api.event.connection.LoginEvent;
import com.velocitypowered.api.event.player.ServerConnectedEvent;
import com.velocitypowered.api.proxy.Player;
import net.afyer.afybroker.core.message.PlayerProxyConnectMessage;
import net.afyer.afybroker.core.message.PlayerProxyDisconnectMessage;
import net.afyer.afybroker.core.message.PlayerServerConnectedMessage;
import net.afyer.afybroker.velocity.AfyBroker;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;

/**
 * @author Nipuru
 * @since 2022/7/30 18:44
 */
public class PlayerListener {

    private final AfyBroker plugin;

    public PlayerListener(AfyBroker plugin) {
        this.plugin = plugin;
    }

    @Subscribe
    public void onConnect(LoginEvent event) {
        try {
            Player player = event.getPlayer();
            PlayerProxyConnectMessage connectMessage = new PlayerProxyConnectMessage()
                    .setUniqueId(player.getUniqueId())
                    .setName(player.getUsername());

            boolean result = plugin.getBrokerClient().invokeSync(connectMessage);

            if (!result) {
                event.setResult(ResultedEvent.ComponentResult.denied(
                        Component.text("登录失败").color(NamedTextColor.RED)));
            }
        } catch (Exception e) {
            plugin.getLogger().error(e.getMessage(), e);
            event.setResult(ResultedEvent.ComponentResult.denied(
                    Component.text("产生了一个错误").color(NamedTextColor.RED)));
        }
    }

    @Subscribe
    public void onDisConnect(DisconnectEvent event) {
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

}
