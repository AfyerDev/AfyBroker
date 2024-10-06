package net.afyer.afybroker.server.processor;

import com.alipay.remoting.AsyncContext;
import com.alipay.remoting.BizContext;
import com.alipay.remoting.rpc.protocol.AsyncUserProcessor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import net.afyer.afybroker.core.BrokerClientType;
import net.afyer.afybroker.core.message.PlayerServerJoinMessage;
import net.afyer.afybroker.server.BrokerServer;
import net.afyer.afybroker.server.aware.BrokerServerAware;
import net.afyer.afybroker.server.event.PlayerServerJoinEvent;
import net.afyer.afybroker.server.proxy.BrokerClientItem;
import net.afyer.afybroker.server.proxy.BrokerPlayer;

import java.util.Objects;

/**
 * @author Nipuru
 * @since 2023/09/29 12:16
 */
@Slf4j
public class PlayerServerJoinBrokerProcessor extends AsyncUserProcessor<PlayerServerJoinMessage> implements BrokerServerAware {

    @Setter
    BrokerServer brokerServer;

    @Override
    public void handleRequest(BizContext bizCtx, AsyncContext asyncCtx, PlayerServerJoinMessage request) {
        BrokerClientItem currentBukkit = brokerServer.getClient(bizCtx);
        if (currentBukkit == null) return;
        if (!Objects.equals(currentBukkit.getType(), BrokerClientType.SERVER)) return;

        BrokerPlayer player = brokerServer.getPlayer(request.getUniqueId());
        if (player == null) return;

        if (log.isDebugEnabled()) {
            log.debug("Received player bukkit join message => player[{}], bukkitClient[{}]",
                    request.getName(), currentBukkit.getName());
        }

        handleBukkitJoin(brokerServer, player, currentBukkit);
    }

    public static void handleBukkitJoin(BrokerServer server, BrokerPlayer player, BrokerClientItem bukkitClient) {
        if (!Objects.equals(bukkitClient.getType(), BrokerClientType.SERVER)) return;

        BrokerClientItem previousBukkit = player.getServer();
        player.setServer(bukkitClient);
        server.getPluginManager().callEvent(new PlayerServerJoinEvent(player, previousBukkit, bukkitClient));
    }

    @Override
    public String interest() {
        return PlayerServerJoinMessage.class.getName();
    }

}
