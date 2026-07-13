package net.afyer.afybroker.core.util;


import com.alipay.remoting.Connection;
import com.alipay.remoting.ConnectionEventProcessor;
import com.alipay.remoting.ConnectionEventType;

/**
 * @author Nipuru
 * @since 2025/09/02 19:00
 */
public interface ConnectionEventTypeProcessor extends ConnectionEventProcessor {
    ConnectionEventType getType();

    static ConnectionEventTypeProcessor wrap(ConnectionEventType type, ConnectionEventProcessor processor) {
        return new ConnectionEventTypeProcessor() {
            @Override
            public void onEvent(String remoteAddress, Connection connection) {
                processor.onEvent(remoteAddress, connection);
            }

            @Override
            public ConnectionEventType getType() {
                return type;
            }
        };
    }
}
