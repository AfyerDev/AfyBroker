package net.afyer.afybroker.server.processor;

import com.alipay.remoting.AsyncContext;
import com.alipay.remoting.BizContext;
import com.alipay.remoting.exception.RemotingException;
import com.alipay.remoting.rpc.protocol.AsyncUserProcessor;
import net.afyer.afybroker.core.BrokerClientType;
import net.afyer.afybroker.core.message.BroadcastChatMessage;
import net.afyer.afybroker.server.BrokerServer;
import net.afyer.afybroker.server.aware.BrokerServerAware;
import net.afyer.afybroker.server.proxy.BrokerClientItem;
import net.afyer.afybroker.server.proxy.BrokerClientManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * @author Nipuru
 * @since 2022/8/10 11:27
 */
public class BroadcastChatBrokerProcessor extends AsyncUserProcessor<BroadcastChatMessage> implements BrokerServerAware {

    private static final Logger LOGGER = LoggerFactory.getLogger(BroadcastChatBrokerProcessor.class);

    private BrokerServer brokerServer;

    public void setBrokerServer(BrokerServer brokerServer) {
        this.brokerServer = brokerServer;
    }

    @Override
    public void handleRequest(BizContext bizCtx, AsyncContext asyncCtx, BroadcastChatMessage request) {
        BrokerClientManager clientProxyManager = brokerServer.getClientManager();
        List<BrokerClientItem> brokerClients = clientProxyManager.getByType(BrokerClientType.SERVER);

        for (BrokerClientItem brokerClient : brokerClients) {
            try {
                brokerClient.oneway(request);
            } catch (RemotingException | InterruptedException e) {
                LOGGER.error(e.getMessage(), e);
            }
        }
    }

    @Override
    public String interest() {
        return BroadcastChatMessage.class.getName();
    }
}
