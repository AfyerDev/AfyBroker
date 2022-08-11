package net.afyer.afybroker.server.processor;

import com.alipay.remoting.AsyncContext;
import com.alipay.remoting.BizContext;
import com.alipay.remoting.exception.RemotingException;
import com.alipay.remoting.rpc.protocol.AsyncUserProcessor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import net.afyer.afybroker.core.BrokerClientType;
import net.afyer.afybroker.core.message.SendPlayerTitleMessage;
import net.afyer.afybroker.server.BrokerServer;
import net.afyer.afybroker.server.aware.BrokerServerAware;
import net.afyer.afybroker.server.util.Util;

/**
 * @author Nipuru
 * @since 2022/8/11 9:02
 */
@Slf4j
public class SendPlayerTitleBrokerProcessor extends AsyncUserProcessor<SendPlayerTitleMessage> implements BrokerServerAware {

    @Setter
    BrokerServer brokerServer;

    @Override
    public void handleRequest(BizContext bizCtx, AsyncContext asyncCtx, SendPlayerTitleMessage request) {
        try {
            Util.forward(brokerServer, BrokerClientType.BUKKIT, request.getPlayer(), request);
        } catch (RemotingException | InterruptedException e) {
            log.error(e.getMessage(), e);
        }
    }

    @Override
    public String interest() {
        return SendPlayerTitleMessage.class.getName();
    }
}
