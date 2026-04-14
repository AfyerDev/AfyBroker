package net.afyer.afybroker.client.processor.connection;

import com.alipay.remoting.Connection;
import com.alipay.remoting.ConnectionEventProcessor;
import net.afyer.afybroker.client.BrokerClient;
import net.afyer.afybroker.client.aware.BrokerClientAware;
import net.afyer.afybroker.core.observability.ConnectionState;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ConnectEventClientProcessor implements ConnectionEventProcessor, BrokerClientAware {

    private static final Logger LOGGER = LoggerFactory.getLogger(ConnectEventClientProcessor.class);

    private BrokerClient brokerClient;

    @Override
    public void setBrokerClient(BrokerClient brokerClient) {
        this.brokerClient = brokerClient;
    }

    @Override
    public void onEvent(String remoteAddress, Connection connection) {
        brokerClient.getObservability().onConnection(ConnectionState.CONNECTED);
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Connection establish! remoteAddress {}", remoteAddress);
        }
    }
}
