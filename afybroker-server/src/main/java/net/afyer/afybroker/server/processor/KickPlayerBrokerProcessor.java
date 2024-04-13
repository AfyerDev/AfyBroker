package net.afyer.afybroker.server.processor;

import com.alipay.remoting.AsyncContext;
import com.alipay.remoting.BizContext;
import com.alipay.remoting.exception.RemotingException;
import com.alipay.remoting.rpc.protocol.AsyncUserProcessor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import net.afyer.afybroker.core.message.KickPlayerMessage;
import net.afyer.afybroker.server.BrokerServer;
import net.afyer.afybroker.server.aware.BrokerServerAware;
import net.afyer.afybroker.server.proxy.BrokerPlayer;

/**
 * @author Nipuru
 * @since 2024/02/03 12:42
 */
@Slf4j
public class KickPlayerBrokerProcessor extends AsyncUserProcessor<KickPlayerMessage> implements BrokerServerAware {

    @Setter
    BrokerServer brokerServer;

    @Override
    public void handleRequest(BizContext bizCtx, AsyncContext asyncCtx, KickPlayerMessage request) {
        BrokerPlayer player = brokerServer.getPlayer(request.getPlayer());
        if (player != null) {
            try {
                player.getBungeeClientProxy().oneway(request);
            } catch (RemotingException | InterruptedException e) {
                log.error(e.getMessage(), e);
            }
        }
    }

    @Override
    public String interest() {
        return KickPlayerMessage.class.getName();
    }
}
