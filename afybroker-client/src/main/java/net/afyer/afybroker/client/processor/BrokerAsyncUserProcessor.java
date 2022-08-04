package net.afyer.afybroker.client.processor;

import com.alipay.remoting.rpc.protocol.AsyncUserProcessor;
import lombok.AccessLevel;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import net.afyer.afybroker.client.BrokerClient;
import net.afyer.afybroker.client.aware.BrokerClientAware;

import java.util.concurrent.Executor;

/**
 * @author Nipuru
 * @since 2022/8/3 18:08
 */
@FieldDefaults(level = AccessLevel.PROTECTED)
public abstract class BrokerAsyncUserProcessor<T> extends AsyncUserProcessor<T> implements BrokerClientAware {

    @Setter
    BrokerClient brokerClient;

    @Override
    public Executor getExecutor() {
        return brokerClient.getBizThread();
    }

}
