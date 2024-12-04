package net.afyer.afybroker.server.processor;


import com.alipay.remoting.BizContext;
import com.alipay.remoting.rpc.protocol.SyncUserProcessor;
import lombok.Setter;
import net.afyer.afybroker.core.message.PlayerProfilePropertyMessage;
import net.afyer.afybroker.server.BrokerServer;
import net.afyer.afybroker.server.aware.BrokerServerAware;
import net.afyer.afybroker.server.proxy.BrokerPlayer;

/**
 * @author Nipuru
 * @since 2024/12/03 10:20
 */
@Setter
public class PlayerProfilePropertyBrokerProcessor extends SyncUserProcessor<PlayerProfilePropertyMessage> implements BrokerServerAware {

    BrokerServer brokerServer;

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
