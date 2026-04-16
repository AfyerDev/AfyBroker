package net.afyer.afybroker.server.processor.connection;

import com.alipay.remoting.Connection;
import com.alipay.remoting.ConnectionEventProcessor;
import com.alipay.remoting.ConnectionEventType;
import net.afyer.afybroker.server.BrokerServer;
import net.afyer.afybroker.server.aware.BrokerServerAware;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ConnectFailedEventBrokerProcessor implements ConnectionEventProcessor, BrokerServerAware {

    private static final Logger LOGGER = LoggerFactory.getLogger(ConnectFailedEventBrokerProcessor.class);

    private BrokerServer brokerServer;

    @Override
    public void setBrokerServer(BrokerServer brokerServer) {
        this.brokerServer = brokerServer;
    }

    @Override
    public void onEvent(String remoteAddress, Connection connection) {
        brokerServer.getObservability().onConnection(ConnectionEventType.CONNECT_FAILED);
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Connection failed! remoteAddress {}", remoteAddress);
        }
    }
}
