package net.afyer.afybroker.bungee.listener;

import com.alipay.remoting.exception.RemotingException;
import net.afyer.afybroker.bungee.AfyBroker;
import net.afyer.afybroker.client.BrokerClient;
import net.afyer.afybroker.core.message.BrokerClientInfoMessage;
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
    public void onConnect(LoginEvent event) {
        BrokerClient brokerClient = plugin.getBrokerClient();
        BrokerClientInfoMessage clientInfo = brokerClient.getClientInfo();

        brokerClient.getBizThread().submit(() -> {
            BrokerPlayerBungeeMessage msg = new BrokerPlayerBungeeMessage()
                    .setClientName(clientInfo.getName())
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
    public void onDisConnect(PlayerDisconnectEvent event) {
        BrokerClient brokerClient = plugin.getBrokerClient();
        BrokerClientInfoMessage clientInfo = brokerClient.getClientInfo();

        brokerClient.getBizThread().submit(() -> {
            BrokerPlayerBungeeMessage msg = new BrokerPlayerBungeeMessage()
                    .setClientName(clientInfo.getName())
                    .setUid(event.getPlayer().getUniqueId())
                    .setState(BrokerPlayerBungeeMessage.State.DISCONNECT);

            try {
                brokerClient.oneway(msg);
            } catch (RemotingException | InterruptedException e) {
                e.printStackTrace();
            }
        });
    }

}
