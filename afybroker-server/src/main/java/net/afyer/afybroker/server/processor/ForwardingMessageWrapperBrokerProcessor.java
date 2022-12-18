package net.afyer.afybroker.server.processor;

import com.alipay.remoting.AsyncContext;
import com.alipay.remoting.BizContext;
import com.alipay.remoting.InvokeCallback;
import com.alipay.remoting.exception.RemotingException;
import com.alipay.remoting.rpc.protocol.AsyncUserProcessor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import net.afyer.afybroker.core.message.ForwardingMessageWrapper;
import net.afyer.afybroker.server.BrokerServer;
import net.afyer.afybroker.server.aware.BrokerServerAware;
import net.afyer.afybroker.server.proxy.BrokerClientProxy;

import java.io.Serializable;
import java.util.concurrent.Executor;

/**
 * @author Nipuru
 * @since 2022/9/4 18:23
 */
@Slf4j
public class ForwardingMessageWrapperBrokerProcessor extends AsyncUserProcessor<ForwardingMessageWrapper> implements BrokerServerAware {

    @Setter
    BrokerServer brokerServer;

    @Override
    public void handleRequest(BizContext bizCtx, AsyncContext asyncCtx, ForwardingMessageWrapper request) {
         BrokerClientProxy target = brokerServer.getBrokerClientProxyManager().getByName(request.getClientName());

         if (target == null) {
             return;
         }

         Serializable message = request.getMessage();

        //如果有回调则异步执行 否则直接oneway
         if (request.isHasResponse()) {
             try {
                 target.invokeWithCallback(message, new InvokeCallback() {
                     @Override
                     public void onResponse(Object result) {
                         asyncCtx.sendResponse(result);
                     }

                     @Override
                     public void onException(Throwable e) {
                         log.error(e.getMessage(), e);
                     }

                     @Override
                     public Executor getExecutor() {
                         return null;
                     }
                 });
             } catch (RemotingException | InterruptedException e) {
                 e.printStackTrace();
             }
         } else {
             try {
                 target.oneway(message);
             } catch (RemotingException | InterruptedException e) {
                 e.printStackTrace();
             }
         }

    }

    @Override
    public String interest() {
        return ForwardingMessageWrapper.class.getName();
    }
}
