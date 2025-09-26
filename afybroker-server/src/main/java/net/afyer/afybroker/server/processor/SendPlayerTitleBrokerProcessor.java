package net.afyer.afybroker.server.processor;

import com.alipay.remoting.AsyncContext;
import com.alipay.remoting.BizContext;
import com.alipay.remoting.exception.RemotingException;
import com.alipay.remoting.rpc.protocol.AsyncUserProcessor;
import net.afyer.afybroker.core.message.SendPlayerTitleMessage;
import net.afyer.afybroker.server.BrokerServer;
import net.afyer.afybroker.server.aware.BrokerServerAware;
import net.afyer.afybroker.server.proxy.BrokerClientItem;
import net.afyer.afybroker.server.proxy.BrokerPlayer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Nipuru
 * @since 2022/8/11 9:02
 */
public class SendPlayerTitleBrokerProcessor extends AsyncUserProcessor<SendPlayerTitleMessage> implements BrokerServerAware {

    private static final Logger LOGGER = LoggerFactory.getLogger(SendPlayerTitleBrokerProcessor.class);

    private BrokerServer brokerServer;

    public void setBrokerServer(BrokerServer brokerServer) {
        this.brokerServer = brokerServer;
    }

    @Override
    public void handleRequest(BizContext bizCtx, AsyncContext asyncCtx, SendPlayerTitleMessage request) {
        BrokerPlayer brokerPlayer = brokerServer.getPlayer(request.getName());
        if (brokerPlayer == null) {
            return;
        }

        BrokerClientItem clientProxy = brokerPlayer.getServer();

        if (clientProxy == null) {
            return;
        }

        try {
            clientProxy.oneway(request);
        } catch (RemotingException | InterruptedException e) {
            LOGGER.error(e.getMessage(), e);
        }
    }

    @Override
    public String interest() {
        return SendPlayerTitleMessage.class.getName();
    }
}
