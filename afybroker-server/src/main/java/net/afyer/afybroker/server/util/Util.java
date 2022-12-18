package net.afyer.afybroker.server.util;

import com.alipay.remoting.exception.RemotingException;
import lombok.experimental.UtilityClass;
import net.afyer.afybroker.core.BrokerClientType;
import net.afyer.afybroker.server.BrokerServer;
import net.afyer.afybroker.server.proxy.BrokerClientProxy;
import net.afyer.afybroker.server.proxy.BrokerPlayer;

import java.util.UUID;

/**
 * @author Nipuru
 * @since 2022/8/11 11:57
 */
@UtilityClass
public class Util {

    public boolean forward(BrokerServer server, BrokerClientType clientType, UUID player, Object request) {
        return forward(clientType, request, server.getBrokerPlayerManager().getPlayer(player));
    }

    public boolean forward(BrokerServer server, BrokerClientType clientType, String player, Object request) {
        return forward(clientType, request, server.getBrokerPlayerManager().getPlayer(player));
    }

    public boolean forward(BrokerClientType clientType, Object request, BrokerPlayer brokerPlayer) {
        if (brokerPlayer == null) {
            return false;
        }

        BrokerClientProxy clientProxy = switch (clientType) {
            case BUNGEE -> brokerPlayer.getBungeeClientProxy();
            case BUKKIT -> brokerPlayer.getBukkitClientProxy();
        };

        if (clientProxy == null) {
            return false;
        }

        try {
            clientProxy.oneway(request);
        } catch (RemotingException | InterruptedException e) {
            e.printStackTrace();
        }
        return true;
    }
}
