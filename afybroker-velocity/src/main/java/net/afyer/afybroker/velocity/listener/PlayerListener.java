package net.afyer.afybroker.velocity.listener;

import com.alipay.remoting.exception.RemotingException;
import com.velocitypowered.api.event.ResultedEvent;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.connection.DisconnectEvent;
import com.velocitypowered.api.event.connection.LoginEvent;
import com.velocitypowered.api.event.player.ServerConnectedEvent;
import com.velocitypowered.api.proxy.Player;
import lombok.AllArgsConstructor;
import net.afyer.afybroker.client.BrokerClient;
import net.afyer.afybroker.core.message.PlayerBukkitConnectedMessage;
import net.afyer.afybroker.core.message.PlayerBungeeConnectMessage;
import net.afyer.afybroker.core.message.PlayerBungeeDisconnectMessage;
import net.afyer.afybroker.velocity.AfyBroker;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;

/**
 * @author Nipuru
 * @since 2022/7/30 18:44
 */
@AllArgsConstructor
public class PlayerListener {

    private final AfyBroker plugin;

    @Subscribe
    public void onConnect(LoginEvent event) {
        try {
            Player player = event.getPlayer();
            PlayerBungeeConnectMessage connectMessage = new PlayerBungeeConnectMessage()
                    .setUid(player.getUniqueId())
                    .setName(player.getUsername());

            boolean result = plugin.getBrokerClient().invokeSync(connectMessage);

            if (!result) {
                event.setResult(ResultedEvent.ComponentResult.denied(
                        Component.text("登录失败").color(NamedTextColor.RED)));
            }
        } catch (Exception ex) {
            event.setResult(ResultedEvent.ComponentResult.denied(
                    Component.text("产生了一个错误").color(NamedTextColor.RED)));
        }
    }

    @Subscribe
    public void onDisConnect(DisconnectEvent event) {
        PlayerBungeeDisconnectMessage msg = new PlayerBungeeDisconnectMessage()
                .setUid(event.getPlayer().getUniqueId())
                .setName(event.getPlayer().getUsername());

        try {
            plugin.getBrokerClient().oneway(msg);
        } catch (RemotingException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Subscribe
    public void onServerConnected(ServerConnectedEvent event) {
        PlayerBukkitConnectedMessage msg = new PlayerBukkitConnectedMessage()
                .setPlayerName(event.getPlayer().getUsername())
                .setPlayerUniqueId(event.getPlayer().getUniqueId())
                .setServerName(event.getServer().getServerInfo().getName());

        try {
            plugin.getBrokerClient().oneway(msg);
        } catch (RemotingException | InterruptedException e) {
            e.printStackTrace();
        }
    }

}
