package net.afyer.afybroker.client.processor;

import com.alipay.remoting.BizContext;
import com.alipay.remoting.rpc.protocol.SyncUserProcessor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import net.afyer.afybroker.client.BrokerClient;
import net.afyer.afybroker.client.aware.BrokerClientAware;
import net.afyer.afybroker.core.message.RequestBrokerClientInfoMessage;

import java.util.concurrent.Executor;

/**
 * @author Nipuru
 * @since 2022/7/30 17:22
 */
@Slf4j
public class RequestBrokerClientInfoClientProcessor extends SyncUserProcessor<RequestBrokerClientInfoMessage> implements BrokerClientAware {

    @Setter
    BrokerClient brokerClient;

    @Override
    public Object handleRequest(BizContext bizCtx, RequestBrokerClientInfoMessage request) throws Exception {
        log.info("Received server request, sending client info");
        return brokerClient.getClientInfo().toMessage();
    }

    @Override
    public String interest() {
        return RequestBrokerClientInfoMessage.class.getName();
    }

    @Override
    public Executor getExecutor() {
        return super.getExecutor();
    }
}
