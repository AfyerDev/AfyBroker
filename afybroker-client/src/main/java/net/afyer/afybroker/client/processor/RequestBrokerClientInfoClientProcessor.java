package net.afyer.afybroker.client.processor;

import com.alipay.remoting.BizContext;
import com.alipay.remoting.rpc.protocol.SyncUserProcessor;
import net.afyer.afybroker.client.BrokerClient;
import net.afyer.afybroker.client.aware.BrokerClientAware;
import net.afyer.afybroker.core.message.RequestBrokerClientInfoMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Nipuru
 * @since 2022/7/30 17:22
 */
public class RequestBrokerClientInfoClientProcessor extends SyncUserProcessor<RequestBrokerClientInfoMessage> implements BrokerClientAware {

    private static final Logger LOGGER = LoggerFactory.getLogger(RequestBrokerClientInfoClientProcessor.class);

    private BrokerClient brokerClient;

    public void setBrokerClient(BrokerClient brokerClient) {
        this.brokerClient = brokerClient;
    }

    @Override
    public Object handleRequest(BizContext bizCtx, RequestBrokerClientInfoMessage request) throws Exception {
        LOGGER.info("Received server request, sending client info");
        return brokerClient.getClientInfo().toMessage();
    }

    @Override
    public String interest() {
        return RequestBrokerClientInfoMessage.class.getName();
    }
}
