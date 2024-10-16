package net.afyer.afybroker.server.processor;

import com.alipay.remoting.AsyncContext;
import com.alipay.remoting.BizContext;
import com.alipay.remoting.rpc.protocol.AsyncUserProcessor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import net.afyer.afybroker.core.message.ConnectToServerMessage;
import net.afyer.afybroker.server.BrokerServer;
import net.afyer.afybroker.server.aware.BrokerServerAware;
import net.afyer.afybroker.server.proxy.BrokerPlayer;

/**
 * @author Nipuru
 * @since 2022/9/6 17:35
 */
@Slf4j
public class ConnectToServerBrokerProcessor extends AsyncUserProcessor<ConnectToServerMessage> implements BrokerServerAware {

    @Setter
    BrokerServer brokerServer;

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
