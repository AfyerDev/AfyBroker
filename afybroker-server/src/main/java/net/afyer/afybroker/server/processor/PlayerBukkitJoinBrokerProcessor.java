package net.afyer.afybroker.server.processor;

import com.alipay.remoting.AsyncContext;
import com.alipay.remoting.BizContext;
import com.alipay.remoting.rpc.protocol.AsyncUserProcessor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
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
        BrokerClientProxy bukkitClient = brokerServer.getBrokerClientProxyManager().getByAddress(bizCtx.getRemoteAddress());
        if (bukkitClient == null) return;

        BrokerPlayer player = brokerServer.getPlayer(request.getUniqueId());
        if (player == null) return;

        if (BrokerGlobalConfig.OPEN_LOG) {
            log.info("Received player bukkit join message (player:{}, bukkitClient:{})",
                    request.getName(), bukkitClient.getName());
        }

        brokerServer.getPluginManager().callEvent(new PlayerBukkitJoinEvent(player, bukkitClient));
    }

    @Override
    public String interest() {
        return PlayerBukkitJoinMessage.class.getName();
    }

}
