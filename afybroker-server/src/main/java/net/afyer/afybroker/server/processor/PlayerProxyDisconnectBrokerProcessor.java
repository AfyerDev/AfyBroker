package net.afyer.afybroker.server.processor;

import com.alipay.remoting.AsyncContext;
import com.alipay.remoting.BizContext;
import com.alipay.remoting.rpc.protocol.AsyncUserProcessor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import net.afyer.afybroker.core.BrokerClientType;
import net.afyer.afybroker.core.message.PlayerProxyDisconnectMessage;
import net.afyer.afybroker.server.BrokerServer;
import net.afyer.afybroker.server.aware.BrokerServerAware;
import net.afyer.afybroker.server.event.PlayerProxyLogoutEvent;
import net.afyer.afybroker.server.proxy.BrokerClientItem;
import net.afyer.afybroker.server.proxy.BrokerPlayer;
import net.afyer.afybroker.server.proxy.BrokerPlayerManager;

import java.util.Objects;
import java.util.UUID;

/**
 * @author Nipuru
 * @since 2022/11/21 17:32
 */
@Slf4j
public class PlayerProxyDisconnectBrokerProcessor extends AsyncUserProcessor<PlayerProxyDisconnectMessage> implements BrokerServerAware {

    @Setter
    BrokerServer brokerServer;

    @Override
    public void handleRequest(BizContext bizCtx, AsyncContext asyncCtx, PlayerProxyDisconnectMessage request) {
        BrokerClientItem playerBungee = brokerServer.getClient(bizCtx);
        if (playerBungee == null) {
            return;
        }
        if (!Objects.equals(playerBungee.getType(), BrokerClientType.PROXY)) {
            return;
        }

        if (log.isDebugEnabled()) {
            log.debug("Received player bungee disconnect message => player[{}], bungeeClient[{}]",
                    request.getName(), playerBungee.getName());
        }

        handlePlayerRemove(brokerServer, request.getUniqueId());
    }

    public static void handlePlayerRemove(BrokerServer brokerServer, UUID uniqueId) {
        BrokerPlayerManager playerManager = brokerServer.getPlayerManager();
        BrokerPlayer brokerPlayer = playerManager.getPlayer(uniqueId);
        if (brokerPlayer != null) {
            brokerServer.getPluginManager().callEvent(new PlayerProxyLogoutEvent(brokerPlayer));
            playerManager.removePlayer(uniqueId);
        }
    }

    @Override
    public String interest() {
        return PlayerProxyDisconnectMessage.class.getName();
    }
}
