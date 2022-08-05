package net.afyer.afybroker.server.processor;

import com.alipay.remoting.AsyncContext;
import com.alipay.remoting.BizContext;
import com.alipay.remoting.exception.RemotingException;
import net.afyer.afybroker.core.message.SendPlayerChatMessage;
import net.afyer.afybroker.server.proxy.BrokerClientProxy;
import net.afyer.afybroker.server.proxy.BrokerPlayer;

/**
 * @author Nipuru
 * @since 2022/8/5 10:07
 */
public class SendPlayerChatBrokerProcessor extends BrokerAsyncUserProcessor<SendPlayerChatMessage> {

    @Override
    public void handleRequest(BizContext bizCtx, AsyncContext asyncCtx, SendPlayerChatMessage request) {
        BrokerPlayer player = brokerServer.getBrokerPlayerManager().getPlayer(request.getUid());
        if (player == null) {
            return;
        }

        BrokerClientProxy clientProxy = switch (request.getType()) {
            case BUNGEE -> player.getBungeeClientProxy();
            case BUKKIT -> player.getBukkitClientProxy();
        };

        if (clientProxy == null) {
            return;
        }

        try {
            clientProxy.oneway(request);
        } catch (RemotingException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public String interest() {
        return SendPlayerChatMessage.class.getName();
    }
}
