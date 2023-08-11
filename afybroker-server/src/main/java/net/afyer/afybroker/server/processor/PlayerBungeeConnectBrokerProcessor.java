package net.afyer.afybroker.server.processor;

import com.alipay.remoting.BizContext;
import com.alipay.remoting.rpc.protocol.SyncUserProcessor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import net.afyer.afybroker.core.BrokerClientType;
import net.afyer.afybroker.core.BrokerGlobalConfig;
import net.afyer.afybroker.core.message.PlayerBungeeConnectMessage;
import net.afyer.afybroker.server.BrokerServer;
import net.afyer.afybroker.server.aware.BrokerServerAware;
import net.afyer.afybroker.server.event.PlayerBungeeLoginEvent;
import net.afyer.afybroker.server.proxy.BrokerClientProxy;
import net.afyer.afybroker.server.proxy.BrokerClientProxyManager;
import net.afyer.afybroker.server.proxy.BrokerPlayer;
import net.afyer.afybroker.server.proxy.BrokerPlayerManager;

/**
 * @author Nipuru
 * @since 2022/8/1 11:41
 */
@Slf4j
public class PlayerBungeeConnectBrokerProcessor extends SyncUserProcessor<PlayerBungeeConnectMessage> implements BrokerServerAware {

    @Setter
    BrokerServer brokerServer;

    @Override
    public Object handleRequest(BizContext bizCtx, PlayerBungeeConnectMessage request) throws Exception {
        BrokerClientProxyManager clientProxyManager = brokerServer.getBrokerClientProxyManager();
        BrokerClientProxy playerBungee = clientProxyManager.getByAddress(bizCtx.getRemoteAddress());
        if (playerBungee == null) {
            return false;
        }
        if (playerBungee.getType() != BrokerClientType.BUNGEE) {
            return false;
        }

        if (BrokerGlobalConfig.OPEN_LOG) {
            log.info("Received player bungee connect message (player:{}, bungeeClient:{})",
                    request.getName(), playerBungee.getName());
        }

        BrokerPlayerManager playerManager = brokerServer.getBrokerPlayerManager();

        BrokerPlayer brokerPlayer = new BrokerPlayer(request.getUid(), request.getName(), playerBungee);
        BrokerPlayer player = playerManager.addPlayer(brokerPlayer);
        brokerServer.getPluginManager().callEvent(new PlayerBungeeLoginEvent(brokerPlayer));
        return player == null;
    }

    @Override
    public String interest() {
        return PlayerBungeeConnectMessage.class.getName();
    }

}
