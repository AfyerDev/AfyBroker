package net.afyer.afybroker.client.processor;

import com.alipay.remoting.AsyncContext;
import com.alipay.remoting.BizContext;
import com.alipay.remoting.rpc.protocol.AsyncUserProcessor;
import net.afyer.afybroker.core.message.CloseBrokerClientMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Nipuru
 * @since 2025/10/04 16:31
 */
public class CloseBrokerClientProcessor extends AsyncUserProcessor<CloseBrokerClientMessage> {

    private static final Logger LOGGER = LoggerFactory.getLogger(CloseBrokerClientProcessor.class);
    private final Closeable closeable;

    public CloseBrokerClientProcessor(Closeable closeable) {

        this.closeable = closeable;
    }

    @Override
    public void handleRequest(BizContext bizCtx, AsyncContext asyncCtx, CloseBrokerClientMessage request) throws Exception {
        LOGGER.info("Received close broker client message.");
        closeable.close();
    }

    @Override
    public String interest() {
        return CloseBrokerClientMessage.class.getName();
    }

    @FunctionalInterface
    public interface Closeable {
        void close() throws Exception;
    }
}
