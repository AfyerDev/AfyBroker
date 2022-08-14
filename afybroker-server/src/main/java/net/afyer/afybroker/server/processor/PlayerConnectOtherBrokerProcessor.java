package net.afyer.afybroker.server.processor;

import com.alipay.remoting.AsyncContext;
import com.alipay.remoting.BizContext;
import com.alipay.remoting.rpc.protocol.AsyncUserProcessor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import net.afyer.afybroker.core.BrokerClientType;
import net.afyer.afybroker.core.message.PlayerConnectOtherMessage;
import net.afyer.afybroker.server.BrokerServer;
import net.afyer.afybroker.server.aware.BrokerServerAware;
import net.afyer.afybroker.server.util.Util;

/**
 * @author Nipuru
 * @since 2022/8/11 11:20
 */
@Slf4j
public class PlayerConnectOtherBrokerProcessor extends AsyncUserProcessor<PlayerConnectOtherMessage> implements BrokerServerAware {

    @Setter
    BrokerServer brokerServer;

    @Override
    public void handleRequest(BizContext bizCtx, AsyncContext asyncCtx, PlayerConnectOtherMessage request) {
        Util.forward(brokerServer, BrokerClientType.BUKKIT, request.getPlayer(), request);
    }

    @Override
    public String interest() {
        return PlayerConnectOtherMessage.class.getName();
    }
}
