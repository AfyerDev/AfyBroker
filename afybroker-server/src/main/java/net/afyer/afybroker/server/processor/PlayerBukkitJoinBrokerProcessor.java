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
import net.afyer.afybroker.server.proxy.BrokerClientProxyManager;
import net.afyer.afybroker.server.proxy.BrokerPlayer;
import net.afyer.afybroker.server.proxy.BrokerPlayerManager;

/**
 * @author Nipuru
 * @since 2022/9/12 12:33
 */
@Slf4j
public class PlayerBukkitJoinBrokerProcessor extends AsyncUserProcessor<PlayerBukkitJoinMessage> implements BrokerServerAware {

    @Setter
    BrokerServer brokerServer;

    @Override
    public void handleRequest(BizContext bizCtx, AsyncContext asyncCtx, PlayerBukkitJoinMessage request) {

        BrokerClientProxyManager clientProxyManager = brokerServer.getBrokerClientProxyManager();
        BrokerClientProxy currentBukkit = clientProxyManager.getByAddress(bizCtx.getRemoteAddress());
        if (currentBukkit == null) return;
        if (currentBukkit.getType() != BrokerClientType.BUKKIT) return;

        if (BrokerGlobalConfig.OPEN_LOG) {
            log.info("Received player bukkit join message (uuid:{}, name:{}, clientName:{}",
                    request.getUid(), request.getName(), currentBukkit.getName());
        }

        BrokerPlayerManager playerManager = brokerServer.getBrokerPlayerManager();

        BrokerPlayer brokerPlayer = playerManager.getPlayer(request.getUid());
        if (brokerPlayer == null) {
            return;
        }
        BrokerClientProxy previousBukkit = brokerPlayer.getBukkitClientProxy();
        brokerPlayer.setBukkitClientProxy(currentBukkit);

        PlayerBukkitJoinEvent event = new PlayerBukkitJoinEvent(brokerPlayer, previousBukkit, currentBukkit);
        brokerServer.getPluginManager().callEvent(event);
    }

    @Override
    public String interest() {
        return PlayerBukkitJoinMessage.class.getName();
    }
}
