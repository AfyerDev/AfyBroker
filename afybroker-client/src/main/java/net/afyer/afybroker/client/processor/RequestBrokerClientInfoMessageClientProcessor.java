package net.afyer.afybroker.client.processor;

import com.alipay.remoting.AsyncContext;
import com.alipay.remoting.BizContext;
import com.alipay.remoting.exception.RemotingException;
import com.alipay.remoting.rpc.protocol.AsyncUserProcessor;
import net.afyer.afybroker.client.BrokerClient;
import net.afyer.afybroker.core.BrokerClientInfoMessage;
import net.afyer.afybroker.core.RequestBrokerClientInfoMessage;

/**
 * @author Nipuru
 * @since 2022/7/30 17:22
 */
public class RequestBrokerClientInfoMessageClientProcessor extends AsyncUserProcessor<RequestBrokerClientInfoMessage> {

    private final BrokerClient brokerClient;

    public RequestBrokerClientInfoMessageClientProcessor(BrokerClient brokerClient) {
        this.brokerClient = brokerClient;
    }

    @Override
    public void handleRequest(BizContext bizCtx, AsyncContext asyncCtx, RequestBrokerClientInfoMessage request) {

        BrokerClientInfoMessage response = new BrokerClientInfoMessage()
                .setName(brokerClient.getName())
                .setTag(brokerClient.getTag())
                .setType(brokerClient.getType());

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

}
