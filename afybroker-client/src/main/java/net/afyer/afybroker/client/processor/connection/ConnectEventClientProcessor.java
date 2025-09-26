package net.afyer.afybroker.client.processor.connection;

import com.alipay.remoting.Connection;
import com.alipay.remoting.ConnectionEventProcessor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Nipuru
 * @since 2022/7/30 11:42
 */
public class ConnectEventClientProcessor implements ConnectionEventProcessor {

    private static final Logger LOGGER = LoggerFactory.getLogger(ConnectEventClientProcessor.class);
    @Override
    public void onEvent(String remoteAddress, Connection connection) {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Connection establish! remoteAddress {}", remoteAddress);
        }
    }
}
