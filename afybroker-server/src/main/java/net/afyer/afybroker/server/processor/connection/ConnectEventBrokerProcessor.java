package net.afyer.afybroker.server.processor.connection;

import com.alipay.remoting.Connection;
import com.alipay.remoting.ConnectionEventProcessor;
import com.alipay.remoting.ConnectionEventType;
import net.afyer.afybroker.server.BrokerServer;
import net.afyer.afybroker.server.aware.BrokerServerAware;
import net.afyer.afybroker.server.event.ClientConnectEvent;

/** 连接事件仅处理连接状态，客户端注册由独立的注册请求完成。 */
public class ConnectEventBrokerProcessor implements ConnectionEventProcessor, BrokerServerAware {
    private BrokerServer brokerServer;

    @Override
    public void setBrokerServer(BrokerServer brokerServer) {
        this.brokerServer = brokerServer;
    }

    @Override
    public void onEvent(String remoteAddress, Connection connection) {
        brokerServer.getObservability().onConnection(ConnectionEventType.CONNECT);
        brokerServer.getPluginManager().callEvent(new ClientConnectEvent(remoteAddress, connection));
    }
}
