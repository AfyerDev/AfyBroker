package net.afyer.afybroker.server.processor;

import com.alipay.remoting.AsyncContext;
import com.alipay.remoting.BizContext;
import com.alipay.remoting.rpc.protocol.AsyncUserProcessor;
import net.afyer.afybroker.core.message.KickPlayerMessage;
import net.afyer.afybroker.server.BrokerServer;
import net.afyer.afybroker.server.aware.BrokerServerAware;
import net.afyer.afybroker.server.proxy.BrokerPlayer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Nipuru
 * @since 2024/02/03 12:42
 */
public class KickPlayerBrokerProcessor extends AsyncUserProcessor<KickPlayerMessage> implements BrokerServerAware {

    private static final Logger LOGGER = LoggerFactory.getLogger(KickPlayerBrokerProcessor.class);

    private BrokerServer brokerServer;

    public void setBrokerServer(BrokerServer brokerServer) {
        this.brokerServer = brokerServer;
    }

    @Override
    public void handleRequest(BizContext bizCtx, AsyncContext asyncCtx, KickPlayerMessage request) throws Exception {
        BrokerPlayer player = brokerServer.getPlayer(request.getUniqueId());
        if (player == null) {
            return;
        }
        player.kick(request.getMessage());
    }

    @Override
    public String interest() {
        return KickPlayerMessage.class.getName();
    }
}
