package net.afyer.afybroker.server.processor;

import com.alipay.remoting.AsyncContext;
import com.alipay.remoting.BizContext;
import com.alipay.remoting.rpc.protocol.AsyncUserProcessor;
import lombok.Setter;
import net.afyer.afybroker.core.message.ConnectToServerMessage;
import net.afyer.afybroker.server.BrokerServer;
import net.afyer.afybroker.server.aware.BrokerServerAware;
import net.afyer.afybroker.server.proxy.BrokerClientProxy;
import net.afyer.afybroker.server.proxy.BrokerPlayer;

/**
 * @author Nipuru
 * @since 2022/9/6 17:35
 */
public class ConnectToServerBrokerProcessor extends AsyncUserProcessor<ConnectToServerMessage> implements BrokerServerAware {

    @Setter
    BrokerServer brokerServer;

    @Override
    public void handleRequest(BizContext bizCtx, AsyncContext asyncCtx, ConnectToServerMessage message) {
        BrokerPlayer brokerPlayer = brokerServer.getBrokerPlayerManager().getPlayer(message.getPlayer());
        if (brokerPlayer == null) return;

        BrokerClientProxy playerBungee = brokerPlayer.getBungeeClientProxy();
        if (playerBungee == null) return;

        playerBungee.oneway(message);
    }

    @Override
    public String interest() {
        return ConnectToServerMessage.class.getName();
    }
}
