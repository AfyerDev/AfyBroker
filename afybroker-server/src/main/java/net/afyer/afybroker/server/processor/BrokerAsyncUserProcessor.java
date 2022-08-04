package net.afyer.afybroker.server.processor;

import com.alipay.remoting.rpc.protocol.AsyncUserProcessor;
import lombok.AccessLevel;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import net.afyer.afybroker.server.BrokerServer;
import net.afyer.afybroker.server.aware.BrokerServerAware;

import java.util.concurrent.Executor;

/**
 * @author Nipuru
 * @since 2022/8/3 18:10
 */
@FieldDefaults(level = AccessLevel.PROTECTED)
public abstract class BrokerAsyncUserProcessor<T> extends AsyncUserProcessor<T> implements BrokerServerAware {

    @Setter
    BrokerServer brokerServer;

    @Override
    public Executor getExecutor() {
        return brokerServer.getBizThread();
    }
}
