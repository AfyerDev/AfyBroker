package net.afyer.afybroker.server.processor;

import com.alipay.remoting.AsyncContext;
import com.alipay.remoting.BizContext;
import com.alipay.remoting.exception.RemotingException;
import com.alipay.remoting.rpc.protocol.AsyncUserProcessor;
import net.afyer.afybroker.core.message.ForwardingMessage;
import net.afyer.afybroker.core.util.AbstractInvokeCallback;
import net.afyer.afybroker.core.util.BoltUtils;
import net.afyer.afybroker.server.BrokerServer;
import net.afyer.afybroker.server.aware.BrokerServerAware;
import net.afyer.afybroker.server.proxy.BrokerClientItem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;

/**
 * @author Nipuru
 * @since 2022/9/4 18:23
 */
public class ForwardingMessageBrokerProcessor extends AsyncUserProcessor<ForwardingMessage> implements BrokerServerAware {

    private static final Logger LOGGER = LoggerFactory.getLogger(ForwardingMessageBrokerProcessor.class);

    private BrokerServer brokerServer;

    public void setBrokerServer(BrokerServer brokerServer) {
        this.brokerServer = brokerServer;
    }

    @Override
    public void handleRequest(BizContext bizCtx, AsyncContext asyncCtx, ForwardingMessage request) {
        BrokerClientItem target = brokerServer.getClient(request.getClientName());

        if (target == null) return;

        Serializable message = request.getMessage();

        try {
            //如果有回调则异步执行 否则直接oneway
            if (BoltUtils.hasResponse(bizCtx)) {
                target.invokeWithCallback(message, new AbstractInvokeCallback() {
                    @Override
                    public void onResponse(Object result) {
                        asyncCtx.sendResponse(result);
                    }

                    @Override
                    public void onException(Throwable e) {
                        asyncCtx.sendException(e);
                    }
                }, bizCtx.getClientTimeout());
            } else {
                target.oneway(message);
            }
        } catch (RemotingException | InterruptedException e) {
            LOGGER.error(e.getMessage(), e);
        }
    }

    @Override
    public String interest() {
        return ForwardingMessage.class.getName();
    }
}
