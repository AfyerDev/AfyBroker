package net.afyer.afybroker.bungee.listener;

import com.alipay.remoting.exception.RemotingException;
import net.afyer.afybroker.bungee.AfyBroker;
import net.afyer.afybroker.client.BrokerClient;
import net.afyer.afybroker.core.message.BrokerPlayerBungeeMessage;
import net.md_5.bungee.api.event.LoginEvent;
import net.md_5.bungee.api.event.PlayerDisconnectEvent;
import net.md_5.bungee.api.event.ServerConnectedEvent;
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
    public void onLogin(LoginEvent event) {
        BrokerClient brokerClient = plugin.getBrokerClient();

        brokerClient.getBizThread().submit(() -> {
            var msg = new BrokerPlayerBungeeMessage()
                    .setData(brokerClient.getClientInfo().getName())
                    .setUid(event.getConnection().getUniqueId())
                    .setState(BrokerPlayerBungeeMessage.State.CONNECT);



            try {
                brokerClient.oneway(msg);
            } catch (RemotingException | InterruptedException e) {
                e.printStackTrace();
            }
        });
    }

    @EventHandler
    public void onPlayerDisConnect(PlayerDisconnectEvent event) {
        BrokerClient brokerClient = plugin.getBrokerClient();

        brokerClient.getBizThread().submit(() -> {
            var msg = new BrokerPlayerBungeeMessage()
                    .setUid(event.getPlayer().getUniqueId())
                    .setState(BrokerPlayerBungeeMessage.State.DISCONNECT);

            try {
                brokerClient.oneway(msg);
            } catch (RemotingException | InterruptedException e) {
                e.printStackTrace();
            }
        });
    }

    @EventHandler
    public void onServerConnect(ServerConnectedEvent event) {
        BrokerClient brokerClient = plugin.getBrokerClient();

        brokerClient.getBizThread().submit(() -> {
            var msg = new BrokerPlayerBungeeMessage()
                    .setData(event.getServer().getInfo().getName())
                    .setUid(event.getPlayer().getUniqueId())
                    .setState(BrokerPlayerBungeeMessage.State.BUNGEE);

            try {
                brokerClient.oneway(msg);
            } catch (RemotingException | InterruptedException e) {
                e.printStackTrace();
            }
        });
    }

}
