package net.afyer.afybroker.server.processor;

import com.alipay.remoting.AsyncContext;
import com.alipay.remoting.BizContext;
import com.alipay.remoting.exception.RemotingException;
import com.alipay.remoting.rpc.protocol.AsyncUserProcessor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import net.afyer.afybroker.core.BrokerClientType;
import net.afyer.afybroker.core.message.BroadcastChatMessage;
import net.afyer.afybroker.server.BrokerServer;
import net.afyer.afybroker.server.aware.BrokerServerAware;
import net.afyer.afybroker.server.proxy.BrokerClientProxy;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Nipuru
 * @since 2022/8/10 11:27
 */
@Slf4j
public class BroadcastChatBrokerProcessor extends AsyncUserProcessor<BroadcastChatMessage> implements BrokerServerAware {

    @Setter
    BrokerServer brokerServer;

    @Override
    public void handleRequest(BizContext bizCtx, AsyncContext asyncCtx, BroadcastChatMessage request) {
        List<BrokerClientProxy> brokerClients = new ArrayList<>();

        BrokerClientType type = switch (request.getType()) {
            case BUKKIT -> BrokerClientType.BUKKIT;
            case BUNGEE -> BrokerClientType.BUNGEE;
        };

        for (BrokerClientProxy brokerClient : brokerServer.getBrokerClientProxyManager().list()) {
            if (brokerClient.getType() == type) {
                brokerClients.add(brokerClient);
            }
        }

        for (BrokerClientProxy brokerClient : brokerClients) {
            try {
                brokerClient.oneway(request);
            } catch (RemotingException | InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public String interest() {
        return BroadcastChatMessage.class.getName();
    }
}
