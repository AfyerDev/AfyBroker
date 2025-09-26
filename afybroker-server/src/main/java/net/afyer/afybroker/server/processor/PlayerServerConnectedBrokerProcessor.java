package net.afyer.afybroker.server.processor;

import com.alipay.remoting.AsyncContext;
import com.alipay.remoting.BizContext;
import com.alipay.remoting.rpc.protocol.AsyncUserProcessor;
import net.afyer.afybroker.core.BrokerClientType;
import net.afyer.afybroker.core.message.PlayerServerConnectedMessage;
import net.afyer.afybroker.server.BrokerServer;
import net.afyer.afybroker.server.aware.BrokerServerAware;
import net.afyer.afybroker.server.event.PlayerServerConnectedEvent;
import net.afyer.afybroker.server.proxy.BrokerClientItem;
import net.afyer.afybroker.server.proxy.BrokerPlayer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;
import java.util.UUID;

/**
 * @author Nipuru
 * @since 2022/9/12 12:33
 */
public class PlayerServerConnectedBrokerProcessor extends AsyncUserProcessor<PlayerServerConnectedMessage> implements BrokerServerAware {

    private static final Logger LOGGER = LoggerFactory.getLogger(PlayerServerConnectedBrokerProcessor.class);

    private BrokerServer brokerServer;

    public void setBrokerServer(BrokerServer brokerServer) {
        this.brokerServer = brokerServer;
    }

    @Override
    public void handleRequest(BizContext bizCtx, AsyncContext asyncCtx, PlayerServerConnectedMessage request) {
        String playerName = request.getName();
        UUID playerUniqueId = request.getUniqueId();
        String bukkitName = request.getServerName();

        BrokerClientItem bungeeClient = brokerServer.getClient(bizCtx);
        if (bungeeClient == null) return;
        if (!Objects.equals(bungeeClient.getType(), BrokerClientType.PROXY)) return;

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Received player bukkit connected message => player[{}], server[{}]",
                    playerName, bukkitName);
        }

        BrokerPlayer brokerPlayer = brokerServer.getPlayer(playerUniqueId);
        if (brokerPlayer == null) {
            return;
        }

        PlayerServerConnectedEvent event = new PlayerServerConnectedEvent(brokerPlayer);
        brokerServer.getPluginManager().callEvent(event);
    }

    @Override
    public String interest() {
        return PlayerServerConnectedMessage.class.getName();
    }
}
