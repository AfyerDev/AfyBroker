package net.afyer.afybroker.server.processor;


import com.alipay.remoting.BizContext;
import com.alipay.remoting.rpc.protocol.SyncUserProcessor;
import net.afyer.afybroker.core.message.PlayerProfilePropertyMessage;
import net.afyer.afybroker.server.BrokerServer;
import net.afyer.afybroker.server.aware.BrokerServerAware;
import net.afyer.afybroker.server.proxy.BrokerPlayer;

/**
 * @author Nipuru
 * @since 2024/12/03 10:20
 */
public class PlayerProfilePropertyBrokerProcessor extends SyncUserProcessor<PlayerProfilePropertyMessage> implements BrokerServerAware {

    private BrokerServer brokerServer;

    public void setBrokerServer(BrokerServer brokerServer) {
        this.brokerServer = brokerServer;
    }

    @Override
    public Object handleRequest(BizContext bizCtx, PlayerProfilePropertyMessage request) throws Exception {
        BrokerPlayer player = brokerServer.getPlayer(request.getUniqueId());
        if (player == null) {
            return false;
        }
        return player.getProxy().invokeSync(request);
    }

    @Override
    public String interest() {
        return PlayerProfilePropertyMessage.class.getName();
    }

}
