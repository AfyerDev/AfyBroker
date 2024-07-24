package net.afyer.afybroker.server.processor;

import com.alipay.remoting.AsyncContext;
import com.alipay.remoting.BizContext;
import com.alipay.remoting.rpc.protocol.AsyncUserProcessor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import net.afyer.afybroker.core.BrokerClientType;
import net.afyer.afybroker.core.BrokerGlobalConfig;
import net.afyer.afybroker.core.message.PlayerBukkitJoinMessage;
import net.afyer.afybroker.server.BrokerServer;
import net.afyer.afybroker.server.aware.BrokerServerAware;
import net.afyer.afybroker.server.event.PlayerBukkitJoinEvent;
import net.afyer.afybroker.server.proxy.BrokerClientProxy;
import net.afyer.afybroker.server.proxy.BrokerPlayer;

/**
 * @author Nipuru
 * @since 2023/09/29 12:16
 */
@Slf4j
public class PlayerBukkitJoinBrokerProcessor extends AsyncUserProcessor<PlayerBukkitJoinMessage> implements BrokerServerAware {

    @Setter
    BrokerServer brokerServer;

    @Override
    public void handleRequest(BizContext bizCtx, AsyncContext asyncCtx, PlayerBukkitJoinMessage request) {
        BrokerClientProxy currentBukkit = brokerServer.getClientProxy(bizCtx);
        if (currentBukkit == null) return;
        if (currentBukkit.getType() != BrokerClientType.BUKKIT) return;

        BrokerPlayer player = brokerServer.getPlayer(request.getUniqueId());
        if (player == null) return;

        if (BrokerGlobalConfig.OPEN_LOG) {
            log.info("Received player bukkit join message => player[{}], bukkitClient[{}]",
                    request.getName(), currentBukkit.getName());
        }

        setBukkitClientProxy(brokerServer, player, currentBukkit);
    }

    public static void setBukkitClientProxy(BrokerServer server, BrokerPlayer player, BrokerClientProxy bukkitClient) {
        BrokerClientProxy previousBukkit = player.getBukkitClientProxy();
        player.setBukkitClientProxy(bukkitClient);

        server.getPluginManager().callEvent(new PlayerBukkitJoinEvent(player, previousBukkit, bukkitClient));
    }

    @Override
    public String interest() {
        return PlayerBukkitJoinMessage.class.getName();
    }

}
