package net.afyer.afybroker.server.processor.connection;

import com.alipay.remoting.Connection;
import com.alipay.remoting.ConnectionEventProcessor;
import com.alipay.remoting.exception.RemotingException;
import lombok.AccessLevel;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import net.afyer.afybroker.core.RequestBrokerClientInfoMessage;
import net.afyer.afybroker.server.BrokerServer;
import net.afyer.afybroker.server.aware.BrokerServerAware;

/**
 * @author Nipuru
 * @since 2022/7/30 11:42
 */
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ConnectEventBrokerProcessor implements ConnectionEventProcessor, BrokerServerAware {

    @Setter
    BrokerServer brokerServer;

    final RequestBrokerClientInfoMessage requestBrokerClientInfoMessage = new RequestBrokerClientInfoMessage();

    @Override
    public void onEvent(String remoteAddress, Connection connection) {
        log.info("BrokerClient remoteAddress : {} connected, sending request client info message", remoteAddress);

        try {
            brokerServer.getRpcServer().oneway(connection, requestBrokerClientInfoMessage);
        } catch (RemotingException e) {
            log.error(e.getMessage(), e);
        }
    }
}
