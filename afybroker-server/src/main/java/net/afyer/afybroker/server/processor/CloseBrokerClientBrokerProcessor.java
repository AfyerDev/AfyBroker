package net.afyer.afybroker.server.processor;

import com.alipay.remoting.AsyncContext;
import com.alipay.remoting.BizContext;
import com.alipay.remoting.rpc.protocol.AsyncUserProcessor;
import net.afyer.afybroker.core.message.CloseBrokerClientMessage;
import net.afyer.afybroker.server.BrokerServer;
import net.afyer.afybroker.server.aware.BrokerServerAware;
import net.afyer.afybroker.server.proxy.BrokerClientItem;
import net.afyer.afybroker.server.proxy.BrokerClientManager;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Nipuru
 * @since 2025/10/04 16:35
 */
public class CloseBrokerClientBrokerProcessor extends AsyncUserProcessor<CloseBrokerClientMessage> implements BrokerServerAware {

    private BrokerServer brokerServer;

    @Override
    public void handleRequest(BizContext bizCtx, AsyncContext asyncCtx, CloseBrokerClientMessage request) throws Exception {
        BrokerClientManager clientManager = brokerServer.getClientManager();
        List<BrokerClientItem> clientsToClose = new ArrayList<>();
        if (request.names != null) {
            for (String name : request.names) {
                clientsToClose.add(clientManager.getByName(name));
            }
        }
        if (request.tags != null) {
            for (String tag : request.tags) {
                clientsToClose.addAll(clientManager.getByTag(tag));
            }
        }
        if (request.types != null) {
            for (String type : request.types) {
                clientsToClose.addAll(clientManager.getByType(type));
            }
        }
        for (BrokerClientItem client : clientsToClose) {
            if (client == null) continue;
            client.shutdown();
        }
    }

    @Override
    public void setBrokerServer(BrokerServer brokerServer) {
        this.brokerServer = brokerServer;
    }

    @Override
    public String interest() {
        return CloseBrokerClientMessage.class.getName();
    }
}
