package net.afyer.afybroker.server.processor.connection;

import com.alipay.remoting.Connection;
import com.alipay.remoting.ConnectionEventProcessor;
import net.afyer.afybroker.server.BrokerServer;
import net.afyer.afybroker.server.aware.BrokerServerAware;
import net.afyer.afybroker.server.event.ClientCloseEvent;
import net.afyer.afybroker.server.proxy.BrokerClientItem;
import net.afyer.afybroker.server.proxy.BrokerClientManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Nipuru
 * @since 2022/7/30 11:42
 */
public class CloseEventBrokerProcessor implements ConnectionEventProcessor, BrokerServerAware {

    private static final Logger LOGGER = LoggerFactory.getLogger(CloseEventBrokerProcessor.class);

    private BrokerServer brokerServer;

    public void setBrokerServer(BrokerServer brokerServer) {
        this.brokerServer = brokerServer;
    }

    @Override
    public void onEvent(String remoteAddress, Connection connection) {

        BrokerClientManager clientManager = brokerServer.getClientManager();
        BrokerClientItem client = clientManager.getByAddress(remoteAddress);
        clientManager.remove(remoteAddress);

        if (client != null) {
            // 清理服务注册
            brokerServer.getServiceRegistry().unregisterClientServices(client);
            
            ClientCloseEvent event = new ClientCloseEvent(remoteAddress, client.getName(), client.getTags(), client.getType());
            brokerServer.getPluginManager().callEvent(event);
        }

        LOGGER.info("BrokerClient[{}] disconnect", remoteAddress);
    }
}
