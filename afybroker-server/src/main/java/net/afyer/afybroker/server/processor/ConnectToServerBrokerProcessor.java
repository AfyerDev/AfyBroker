package net.afyer.afybroker.server.processor;

import com.alipay.remoting.AsyncContext;
import com.alipay.remoting.BizContext;
import com.alipay.remoting.rpc.protocol.AsyncUserProcessor;
import net.afyer.afybroker.core.message.ConnectToServerMessage;
import net.afyer.afybroker.server.BrokerServer;
import net.afyer.afybroker.server.aware.BrokerServerAware;
import net.afyer.afybroker.server.proxy.BrokerPlayer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Nipuru
 * @since 2022/9/6 17:35
 */
public class ConnectToServerBrokerProcessor extends AsyncUserProcessor<ConnectToServerMessage> implements BrokerServerAware {

    private static final Logger LOGGER = LoggerFactory.getLogger(ConnectToServerBrokerProcessor.class);

    private BrokerServer brokerServer;

    public void setBrokerServer(BrokerServer brokerServer) {
        this.brokerServer = brokerServer;
    }

    @Override
    public void handleRequest(BizContext bizCtx, AsyncContext asyncCtx, ConnectToServerMessage message) throws Exception {
        BrokerPlayer brokerPlayer = brokerServer.getPlayerManager().getPlayer(message.getUniqueId());
        if (brokerPlayer == null) return;

        brokerPlayer.connectToServer(message.getServerName());
    }

    @Override
    public String interest() {
        return ConnectToServerMessage.class.getName();
    }
}
