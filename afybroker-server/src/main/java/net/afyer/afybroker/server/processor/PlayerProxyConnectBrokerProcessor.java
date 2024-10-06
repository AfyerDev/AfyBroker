package net.afyer.afybroker.server.processor;

import com.alipay.remoting.BizContext;
import com.alipay.remoting.rpc.protocol.SyncUserProcessor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import net.afyer.afybroker.core.BrokerClientType;
import net.afyer.afybroker.core.message.PlayerProxyConnectMessage;
import net.afyer.afybroker.server.BrokerServer;
import net.afyer.afybroker.server.aware.BrokerServerAware;
import net.afyer.afybroker.server.event.PlayerProxyLoginEvent;
import net.afyer.afybroker.server.proxy.BrokerClientItem;
import net.afyer.afybroker.server.proxy.BrokerPlayer;
import net.afyer.afybroker.server.proxy.BrokerPlayerManager;

import java.util.Objects;

/**
 * @author Nipuru
 * @since 2022/8/1 11:41
 */
@Slf4j
public class PlayerProxyConnectBrokerProcessor extends SyncUserProcessor<PlayerProxyConnectMessage> implements BrokerServerAware {

    @Setter
    BrokerServer brokerServer;

    @Override
    public Object handleRequest(BizContext bizCtx, PlayerProxyConnectMessage request) throws Exception {
        BrokerClientItem playerBungee = brokerServer.getClient(bizCtx);
        if (playerBungee == null) {
            return false;
        }
        if (!Objects.equals(playerBungee.getType(), BrokerClientType.PROXY)) {
            return false;
        }

        if (log.isDebugEnabled()) {
            log.debug("Received player bungee connect message => player[{}], bungeeClient[{}]",
                    request.getName(), playerBungee.getName());
        }

        BrokerPlayer brokerPlayer = new BrokerPlayer(request.getUniqueId(), request.getName(), playerBungee);
        return handlePlayerAdd(brokerServer, brokerPlayer);
    }

    public static boolean handlePlayerAdd(BrokerServer brokerServer, BrokerPlayer brokerPlayer) {
        BrokerPlayerManager playerManager = brokerServer.getPlayerManager();
        BrokerPlayer player = playerManager.addPlayer(brokerPlayer);
        boolean success = player == null;
        if (success) {
            brokerServer.getPluginManager().callEvent(new PlayerProxyLoginEvent(brokerPlayer));
        }
        return success;
    }

    @Override
    public String interest() {
        return PlayerProxyConnectMessage.class.getName();
    }

}
