package net.afyer.afybroker.server.util;

import net.afyer.afybroker.server.proxy.BrokerClientProxy;

import java.io.Serial;
import java.util.ArrayList;
import java.util.Collection;
import java.util.function.Consumer;

/**
 * 代表 一组客户端代理
 *
 * @author Nipuru
 * @since 2022/11/26 18:01
 */
public class BrokerClientProxies extends ArrayList<BrokerClientProxy> {

    @Serial
    private static final long serialVersionUID = 694390960241413698L;
    private static final Consumer<Throwable> DEFAULT_EXCEPTION_CONSUMER = Throwable::printStackTrace;

    public BrokerClientProxies(int initialCapacity) {
        super(initialCapacity);
    }

    public BrokerClientProxies(Collection<BrokerClientProxy> values) {
        super(values);
    }

    public BrokerClientProxies() {
        super();
    }

    public void oneway(Object message) {
        this.oneway(message, DEFAULT_EXCEPTION_CONSUMER);
    }

    public void oneway(Object message, Consumer<Throwable> exceptionConsumer) {
        for (BrokerClientProxy brokerClientProxy : this) {
            try {
                brokerClientProxy.oneway(message);
            } catch (Throwable throwable) {
                exceptionConsumer.accept(throwable);
            }
        }
    }
}
