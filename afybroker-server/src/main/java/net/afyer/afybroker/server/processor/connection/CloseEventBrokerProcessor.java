package net.afyer.afybroker.server.processor.connection;

import com.alipay.remoting.Connection;
import com.alipay.remoting.ConnectionEventProcessor;
import lombok.AccessLevel;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import net.afyer.afybroker.server.BrokerServer;
import net.afyer.afybroker.server.aware.BrokerServerAware;
import net.afyer.afybroker.server.event.ClientCloseEvent;
import net.afyer.afybroker.server.proxy.BrokerClientItem;
import net.afyer.afybroker.server.proxy.BrokerClientManager;

/**
 * @author Nipuru
 * @since 2022/7/30 11:42
 */
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CloseEventBrokerProcessor implements ConnectionEventProcessor, BrokerServerAware {

    @Setter
    BrokerServer brokerServer;

    @Override
    public void onEvent(String remoteAddress, Connection connection) {

        BrokerClientManager clientProxyManager = brokerServer.getClientManager();
        BrokerClientItem brokerClientItem = clientProxyManager.getByAddress(remoteAddress);
        clientProxyManager.remove(remoteAddress);

        if (brokerClientItem != null) {
            ClientCloseEvent event = new ClientCloseEvent(remoteAddress, brokerClientItem.getName(), brokerClientItem.getTags(), brokerClientItem.getType());
            brokerServer.getPluginManager().callEvent(event);
        }

        log.info("BrokerClient[{}] disconnect", remoteAddress);
    }
}
