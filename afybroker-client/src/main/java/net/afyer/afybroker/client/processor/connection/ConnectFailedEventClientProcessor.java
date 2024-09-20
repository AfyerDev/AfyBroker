package net.afyer.afybroker.client.processor.connection;

import com.alipay.remoting.Connection;
import com.alipay.remoting.ConnectionEventProcessor;
import lombok.extern.slf4j.Slf4j;
import net.afyer.afybroker.core.BrokerGlobalConfig;

/**
 * @author Nipuru
 * @since 2022/7/30 11:42
 */
@Slf4j
public class ConnectFailedEventClientProcessor implements ConnectionEventProcessor {
    @Override
    public void onEvent(String remoteAddress, Connection connection) {
        if (log.isDebugEnabled()) {
            log.debug("Connection failed! remoteAddress {}", remoteAddress);
        }
    }
}
