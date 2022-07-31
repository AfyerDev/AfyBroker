package net.afyer.afybroker.server.processor;

import com.alipay.remoting.AsyncContext;
import com.alipay.remoting.BizContext;
import com.alipay.remoting.rpc.protocol.AsyncUserProcessor;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import net.afyer.afybroker.core.BrokerClientInfoMessage;
import net.afyer.afybroker.server.BrokerServer;
import net.afyer.afybroker.server.proxy.BrokerClientProxy;

import java.util.concurrent.Executor;

/**
 * @author Nipuru
 * @since 2022/7/30 17:24
 */
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE)
public class RegisterBrokerClientInfoBrokerProcessor extends AsyncUserProcessor<BrokerClientInfoMessage> {

    final BrokerServer server;

    public RegisterBrokerClientInfoBrokerProcessor(BrokerServer server) {
        this.server = server;
    }

    @Override
    public void handleRequest(BizContext bizCtx, AsyncContext asyncCtx, BrokerClientInfoMessage request) {
        request.setAddress(bizCtx.getRemoteAddress());

        BrokerClientProxy brokerClientProxy = new BrokerClientProxy(request, server.getRpcServer());
        server.getBrokerClientProxyManager().register(brokerClientProxy);

        log.info("BrokerClient remoteAddress : {} successfully registered", bizCtx.getRemoteAddress());
    }

    @Override
    public String interest() {
        return BrokerClientInfoMessage.class.getName();
    }

    @Override
    public Executor getExecutor() {
        return server.getBizThread();
    }
}
