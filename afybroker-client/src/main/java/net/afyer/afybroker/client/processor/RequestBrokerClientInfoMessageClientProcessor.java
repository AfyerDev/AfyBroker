package net.afyer.afybroker.client.processor;

import com.alipay.remoting.AsyncContext;
import com.alipay.remoting.BizContext;
import com.alipay.remoting.exception.RemotingException;
import com.alipay.remoting.rpc.protocol.AsyncUserProcessor;
import lombok.AccessLevel;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import net.afyer.afybroker.client.BrokerClient;
import net.afyer.afybroker.client.aware.BrokerClientAware;
import net.afyer.afybroker.core.BrokerClientInfoMessage;
import net.afyer.afybroker.core.RequestBrokerClientInfoMessage;

import java.util.concurrent.Executor;

/**
 * @author Nipuru
 * @since 2022/7/30 17:22
 */
@FieldDefaults(level = AccessLevel.PRIVATE)
public class RequestBrokerClientInfoMessageClientProcessor extends AsyncUserProcessor<RequestBrokerClientInfoMessage> implements BrokerClientAware {

    @Setter
    BrokerClient brokerClient;

    @Override
    public void handleRequest(BizContext bizCtx, AsyncContext asyncCtx, RequestBrokerClientInfoMessage request) {

        BrokerClientInfoMessage response = brokerClient.getClientInfo();

        try {
            brokerClient.getRpcClient().oneway(bizCtx.getConnection(), response);
        } catch (RemotingException e) {
            e.printStackTrace();
        }
    }

    @Override
    public String interest() {
        return RequestBrokerClientInfoMessage.class.getName();
    }

    @Override
    public Executor getExecutor() {
        return brokerClient.getBizThread();
    }
}
