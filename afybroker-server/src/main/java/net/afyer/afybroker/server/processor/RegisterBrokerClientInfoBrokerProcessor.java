package net.afyer.afybroker.server.processor;

import com.alipay.remoting.AsyncContext;
import com.alipay.remoting.BizContext;
import com.alipay.remoting.rpc.protocol.AsyncUserProcessor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import net.afyer.afybroker.core.message.BrokerClientInfoMessage;
import net.afyer.afybroker.server.BrokerServer;
import net.afyer.afybroker.server.aware.BrokerServerAware;
import net.afyer.afybroker.server.proxy.BrokerClientProxy;

/**
 * @author Nipuru
 * @since 2022/7/30 17:24
 */
@Slf4j
public class RegisterBrokerClientInfoBrokerProcessor extends AsyncUserProcessor<BrokerClientInfoMessage> implements BrokerServerAware {

    @Setter
    BrokerServer brokerServer;

    @Override
    public void handleRequest(BizContext bizCtx, AsyncContext asyncCtx, BrokerClientInfoMessage request) {
        request.setAddress(bizCtx.getRemoteAddress());

        BrokerClientProxy brokerClientProxy = new BrokerClientProxy(request, brokerServer.getRpcServer());
        brokerServer.getBrokerClientProxyManager().register(brokerClientProxy);

        log.info("BrokerClient remoteAddress : {} successfully registered", bizCtx.getRemoteAddress());
    }

    @Override
    public String interest() {
        return BrokerClientInfoMessage.class.getName();
    }

}
