package net.afyer.afybroker.core.util;


import com.alipay.remoting.ConnectionEventProcessor;
import com.alipay.remoting.ConnectionEventType;

/**
 * @author Nipuru
 * @since 2025/09/02 19:00
 */
public interface ConnectionEventTypeProcessor extends ConnectionEventProcessor {
    ConnectionEventType getType();
}
