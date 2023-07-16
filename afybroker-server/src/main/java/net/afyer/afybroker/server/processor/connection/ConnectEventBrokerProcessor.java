package net.afyer.afybroker.server.processor.connection;

import com.alipay.remoting.Connection;
import com.alipay.remoting.ConnectionEventProcessor;
import com.alipay.remoting.exception.RemotingException;
import lombok.AccessLevel;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import net.afyer.afybroker.core.BrokerGlobalConfig;
import net.afyer.afybroker.core.message.BrokerClientInfoMessage;
import net.afyer.afybroker.core.message.RequestBrokerClientInfoMessage;
import net.afyer.afybroker.core.util.AbstractInvokeCallback;
import net.afyer.afybroker.server.BrokerServer;
import net.afyer.afybroker.server.aware.BrokerServerAware;
import net.afyer.afybroker.server.event.BrokerClientConnectEvent;
import net.afyer.afybroker.server.event.BrokerClientRegisterEvent;
import net.afyer.afybroker.server.proxy.BrokerClientProxy;

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
        log.info("BrokerClient:{} connected, sending request client info message", remoteAddress);

        try {
            brokerServer.getRpcServer().invokeWithCallback(connection, requestBrokerClientInfoMessage, new AbstractInvokeCallback() {
                @Override
                public void onResponse(Object result) {
                    BrokerClientInfoMessage clientInfoMessage = (BrokerClientInfoMessage) result;
                    clientInfoMessage.setAddress(remoteAddress);

                    BrokerClientProxy brokerClientProxy = new BrokerClientProxy(clientInfoMessage, brokerServer.getRpcServer());
                    brokerServer.getBrokerClientProxyManager().register(brokerClientProxy);

                    BrokerClientRegisterEvent event = new BrokerClientRegisterEvent(clientInfoMessage, brokerClientProxy);
                    brokerServer.getPluginManager().callEvent(event);

                    log.info("BrokerClient:{} successfully registered", remoteAddress);
                }

                @Override
                public void onException(Throwable e) {
                    log.info("BrokerClient:{} registration failed", remoteAddress);
                    log.error(e.getMessage(), e);
                }
            }, BrokerGlobalConfig.DEFAULT_TIMEOUT_MILLIS);
        } catch (RemotingException e) {
            log.info("BrokerClient:{} registration failed", remoteAddress);
            log.error(e.getMessage(), e);
        }

        BrokerClientConnectEvent event = new BrokerClientConnectEvent(remoteAddress, connection);
        brokerServer.getPluginManager().callEvent(event);
    }
}
