package net.afyer.afybroker.client.processor.connection;

import com.alipay.remoting.Connection;
import com.alipay.remoting.ConnectionEventProcessor;
import com.alipay.remoting.ConnectionEventType;
import net.afyer.afybroker.client.BrokerClient;
import net.afyer.afybroker.client.aware.BrokerClientAware;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Nipuru
 * @since 2022/7/30 11:42
 */
public class CloseEventClientProcessor implements ConnectionEventProcessor, BrokerClientAware {

    private static final Logger LOGGER = LoggerFactory.getLogger(CloseEventClientProcessor.class);

    private BrokerClient brokerClient;

    @Override
    public void setBrokerClient(BrokerClient brokerClient) {
        this.brokerClient = brokerClient;
    }

    @Override
    public void onEvent(String remoteAddress, Connection connection) {
        brokerClient.getObservability().onConnection(ConnectionEventType.CLOSE);
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Connection close, remoteAddress {}", remoteAddress);
        }
    }
}
