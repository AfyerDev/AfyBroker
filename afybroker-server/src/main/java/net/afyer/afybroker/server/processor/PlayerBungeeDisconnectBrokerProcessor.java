package net.afyer.afybroker.server.processor;

import com.alipay.remoting.AsyncContext;
import com.alipay.remoting.BizContext;
import com.alipay.remoting.rpc.protocol.AsyncUserProcessor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import net.afyer.afybroker.core.BrokerClientType;
import net.afyer.afybroker.core.BrokerGlobalConfig;
import net.afyer.afybroker.core.message.PlayerBungeeDisconnectMessage;
import net.afyer.afybroker.server.BrokerServer;
import net.afyer.afybroker.server.aware.BrokerServerAware;
import net.afyer.afybroker.server.event.PlayerBungeeLogoutEvent;
import net.afyer.afybroker.server.proxy.BrokerClientProxy;
import net.afyer.afybroker.server.proxy.BrokerClientProxyManager;
import net.afyer.afybroker.server.proxy.BrokerPlayerManager;

/**
 * @author Nipuru
 * @since 2022/11/21 17:32
 */
@Slf4j
public class PlayerBungeeDisconnectBrokerProcessor extends AsyncUserProcessor<PlayerBungeeDisconnectMessage> implements BrokerServerAware {

    @Setter
    BrokerServer brokerServer;

    @Override
    public void handleRequest(BizContext bizCtx, AsyncContext asyncCtx, PlayerBungeeDisconnectMessage request) {
        BrokerClientProxyManager clientProxyManager = brokerServer.getBrokerClientProxyManager();
        BrokerClientProxy playerBungee = clientProxyManager.getByAddress(bizCtx.getRemoteAddress());
        if (playerBungee == null) {
            return;
        }
        if (playerBungee.getType() != BrokerClientType.BUNGEE) {
            return;
        }

        if (BrokerGlobalConfig.OPEN_LOG) {
            log.info("Received player bungee disconnect message (uuid:{}, name:{}, clientName:{}",
                    request.getUid(), request.getName(), playerBungee.getName());
        }

        BrokerPlayerManager playerManager = brokerServer.getBrokerPlayerManager();
        playerManager.removePlayer(request.getUid());
        brokerServer.getPluginManager().callEvent(new PlayerBungeeLogoutEvent(request.getUid(), request.getName()));
    }

    @Override
    public String interest() {
        return PlayerBungeeDisconnectMessage.class.getName();
    }
}
