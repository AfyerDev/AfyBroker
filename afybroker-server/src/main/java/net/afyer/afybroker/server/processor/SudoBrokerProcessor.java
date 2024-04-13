package net.afyer.afybroker.server.processor;

import com.alipay.remoting.AsyncContext;
import com.alipay.remoting.BizContext;
import com.alipay.remoting.exception.RemotingException;
import com.alipay.remoting.rpc.protocol.AsyncUserProcessor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import net.afyer.afybroker.core.BrokerClientType;
import net.afyer.afybroker.core.message.SudoMessage;
import net.afyer.afybroker.server.BrokerServer;
import net.afyer.afybroker.server.aware.BrokerServerAware;
import net.afyer.afybroker.server.proxy.BrokerClientProxy;
import net.afyer.afybroker.server.proxy.BrokerPlayer;

/**
 * @author Nipuru
 * @since 2022/8/12 15:58
 */
@Slf4j
public class SudoBrokerProcessor extends AsyncUserProcessor<SudoMessage> implements BrokerServerAware {

    @Setter
    BrokerServer brokerServer;

    @Override
    public void handleRequest(BizContext bizCtx, AsyncContext asyncCtx, SudoMessage request) {
        BrokerPlayer brokerPlayer = brokerServer.getPlayer(request.getPlayer());
        if (brokerPlayer == null) {
            return;
        }

        BrokerClientProxy clientProxy = null;
        if (request.getType() == BrokerClientType.BUNGEE) {
            clientProxy = brokerPlayer.getBungeeClientProxy();
        } else if (request.getType() == BrokerClientType.BUKKIT) {
            clientProxy = brokerPlayer.getBukkitClientProxy();
        }

        if (clientProxy == null) {
            return;
        }

        try {
            clientProxy.oneway(request);
        } catch (RemotingException | InterruptedException e) {
            log.error(e.getMessage(), e);
        }
    }

    @Override
    public String interest() {
        return SudoMessage.class.getName();
    }
}
