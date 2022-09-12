package net.afyer.afybroker.server.processor;

import com.alipay.remoting.AsyncContext;
import com.alipay.remoting.BizContext;
import com.alipay.remoting.rpc.protocol.AsyncUserProcessor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import net.afyer.afybroker.core.BrokerClientType;
import net.afyer.afybroker.core.BrokerGlobalConfig;
import net.afyer.afybroker.core.message.PlayerBungeeConnectionMessage;
import net.afyer.afybroker.server.BrokerServer;
import net.afyer.afybroker.server.aware.BrokerServerAware;
import net.afyer.afybroker.server.event.PlayerBungeeLoginEvent;
import net.afyer.afybroker.server.event.PlayerBungeeLogoutEvent;
import net.afyer.afybroker.server.proxy.BrokerClientProxy;
import net.afyer.afybroker.server.proxy.BrokerClientProxyManager;
import net.afyer.afybroker.server.proxy.BrokerPlayer;
import net.afyer.afybroker.server.proxy.BrokerPlayerManager;

/**
 * @author Nipuru
 * @since 2022/8/1 11:41
 */
@Slf4j
public class PlayerBungeeBrokerProcessor extends AsyncUserProcessor<PlayerBungeeConnectionMessage> implements BrokerServerAware {

    private static final boolean SUCCESS = true;
    private static final boolean FAILED = false;

    @Setter
    BrokerServer brokerServer;

    @Override
    public void handleRequest(BizContext bizCtx, AsyncContext asyncCtx, PlayerBungeeConnectionMessage request) {

        BrokerClientProxyManager clientProxyManager = brokerServer.getBrokerClientProxyManager();
        BrokerClientProxy playerBungee = clientProxyManager.getByAddress(bizCtx.getRemoteAddress());
        if (playerBungee == null) return;
        if (playerBungee.getType() != BrokerClientType.BUNGEE) return;

        if (BrokerGlobalConfig.OPEN_LOG) {
            log.info("Received player bungee message (uuid:{}, name:{}, state:{}, clientName:{}",
                    request.getUid(), request.getName(), request.getState(), playerBungee.getName());
        }

        BrokerPlayerManager playerManager = brokerServer.getBrokerPlayerManager();

        switch (request.getState()) {
            case PlayerBungeeConnectionMessage.CONNECT -> {
                BrokerPlayer brokerPlayer = new BrokerPlayer(request.getUid(), request.getName());
                brokerPlayer.setBungeeClientProxy(playerBungee);
                BrokerPlayer player = playerManager.addPlayer(brokerPlayer);
                brokerServer.getPluginManager().callEvent(new PlayerBungeeLoginEvent(request.getUid(), request.getName()));
                if (player == null) {
                    asyncCtx.sendResponse(SUCCESS);
                } else {
                    asyncCtx.sendResponse(FAILED);
                }
            }
            case PlayerBungeeConnectionMessage.DISCONNECT -> {
                playerManager.removePlayer(request.getUid());
                brokerServer.getPluginManager().callEvent(new PlayerBungeeLogoutEvent(request.getUid(), request.getName()));
            }
        }
    }

    @Override
    public String interest() {
        return PlayerBungeeConnectionMessage.class.getName();
    }

}
