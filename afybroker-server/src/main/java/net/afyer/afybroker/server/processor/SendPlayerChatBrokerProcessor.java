package net.afyer.afybroker.server.processor;

import com.alipay.remoting.AsyncContext;
import com.alipay.remoting.BizContext;
import com.alipay.remoting.exception.RemotingException;
import com.alipay.remoting.rpc.protocol.AsyncUserProcessor;
import net.afyer.afybroker.core.message.SendPlayerMessageMessage;
import net.afyer.afybroker.server.BrokerServer;
import net.afyer.afybroker.server.aware.BrokerServerAware;
import net.afyer.afybroker.server.proxy.BrokerClientItem;
import net.afyer.afybroker.server.proxy.BrokerPlayer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Nipuru
 * @since 2022/8/5 10:07
 */
public class SendPlayerChatBrokerProcessor extends AsyncUserProcessor<SendPlayerMessageMessage> implements BrokerServerAware {

    private static final Logger LOGGER = LoggerFactory.getLogger(SendPlayerChatBrokerProcessor.class);

    private BrokerServer brokerServer;

    public void setBrokerServer(BrokerServer brokerServer) {
        this.brokerServer = brokerServer;
    }

    @Override
    public void handleRequest(BizContext bizCtx, AsyncContext asyncCtx, SendPlayerMessageMessage request) {
        BrokerPlayer brokerPlayer = brokerServer.getPlayer(request.getUniqueId());
        if (brokerPlayer == null) return;
        BrokerClientItem clientProxy = brokerPlayer.getServer();
        if (clientProxy == null) return;
        try {
            clientProxy.oneway(request);
        } catch (RemotingException | InterruptedException e) {
            LOGGER.error(e.getMessage(), e);
        }
    }

    @Override
    public String interest() {
        return SendPlayerMessageMessage.class.getName();
    }
}
