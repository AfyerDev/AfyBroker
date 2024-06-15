package net.afyer.afybroker.server.processor;

import com.alipay.remoting.AsyncContext;
import com.alipay.remoting.BizContext;
import com.alipay.remoting.rpc.protocol.AsyncUserProcessor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import net.afyer.afybroker.core.BrokerClientType;
import net.afyer.afybroker.core.BrokerGlobalConfig;
import net.afyer.afybroker.core.message.PlayerBukkitConnectedMessage;
import net.afyer.afybroker.server.BrokerServer;
import net.afyer.afybroker.server.aware.BrokerServerAware;
import net.afyer.afybroker.server.event.PlayerBukkitConnectedEvent;
import net.afyer.afybroker.server.proxy.BrokerClientProxy;
import net.afyer.afybroker.server.proxy.BrokerClientProxyManager;
import net.afyer.afybroker.server.proxy.BrokerPlayer;
import net.afyer.afybroker.server.proxy.BrokerPlayerManager;

import java.util.UUID;

/**
 * @author Nipuru
 * @since 2022/9/12 12:33
 */
@Slf4j
public class PlayerBukkitConnectedBrokerProcessor extends AsyncUserProcessor<PlayerBukkitConnectedMessage> implements BrokerServerAware {

    @Setter
    BrokerServer brokerServer;

    @Override
    public void handleRequest(BizContext bizCtx, AsyncContext asyncCtx, PlayerBukkitConnectedMessage request) {
        String playerName = request.getPlayerName();
        UUID playerUniqueId = request.getPlayerUniqueId();
        String bukkitName = request.getServerName();

        BrokerClientProxyManager clientProxyManager = brokerServer.getBrokerClientProxyManager();
        BrokerClientProxy bungeeClient = clientProxyManager.getByAddress(bizCtx.getRemoteAddress());
        if (bungeeClient == null) return;
        if (bungeeClient.getType() != BrokerClientType.BUNGEE) return;

        if (BrokerGlobalConfig.OPEN_LOG) {
            log.info("Received player bukkit connected message => player[{}], server[{}]",
                    playerName, bukkitName);
        }

        BrokerPlayerManager playerManager = brokerServer.getBrokerPlayerManager();
        BrokerPlayer brokerPlayer = playerManager.getPlayer(playerUniqueId);
        if (brokerPlayer == null) {
            return;
        }

        PlayerBukkitConnectedEvent event = new PlayerBukkitConnectedEvent(brokerPlayer);
        brokerServer.getPluginManager().callEvent(event);
    }

    @Override
    public String interest() {
        return PlayerBukkitConnectedMessage.class.getName();
    }
}
