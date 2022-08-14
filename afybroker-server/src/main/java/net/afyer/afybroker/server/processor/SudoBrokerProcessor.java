package net.afyer.afybroker.server.processor;

import com.alipay.remoting.AsyncContext;
import com.alipay.remoting.BizContext;
import com.alipay.remoting.rpc.protocol.AsyncUserProcessor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import net.afyer.afybroker.core.message.SudoMessage;
import net.afyer.afybroker.server.BrokerServer;
import net.afyer.afybroker.server.aware.BrokerServerAware;
import net.afyer.afybroker.server.util.Util;

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
        Util.forward(brokerServer, request.getType(), request.getPlayer(), request);
    }

    @Override
    public String interest() {
        return SudoMessage.class.getName();
    }
}
