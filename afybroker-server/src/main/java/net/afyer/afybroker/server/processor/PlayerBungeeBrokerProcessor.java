package net.afyer.afybroker.server.processor;

import com.alipay.remoting.AsyncContext;
import com.alipay.remoting.BizContext;
import com.alipay.remoting.rpc.protocol.AsyncUserProcessor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import net.afyer.afybroker.core.BrokerGlobalConfig;
import net.afyer.afybroker.core.message.PlayerBungeeMessage;
import net.afyer.afybroker.server.BrokerServer;
import net.afyer.afybroker.server.aware.BrokerServerAware;
import net.afyer.afybroker.server.event.PlayerBukkitFirstJoinEvent;
import net.afyer.afybroker.server.event.PlayerBungeeLoginEvent;
import net.afyer.afybroker.server.event.PlayerBungeeLogoutEvent;
import net.afyer.afybroker.server.proxy.BrokerPlayer;
import net.afyer.afybroker.server.proxy.BrokerPlayerManager;

/**
 * @author Nipuru
 * @since 2022/8/1 11:41
 */
@Slf4j
public class PlayerBungeeBrokerProcessor extends AsyncUserProcessor<PlayerBungeeMessage> implements BrokerServerAware {

    private static final boolean SUCCESS = true;
    private static final boolean FAILED = false;

    @Setter
    BrokerServer brokerServer;

    @Override
    public void handleRequest(BizContext bizCtx, AsyncContext asyncCtx, PlayerBungeeMessage request) {

        if (BrokerGlobalConfig.OPEN_LOG) {
            log.info("Received player message (uuid:{}, name:{}, state:{}, clientName:{}",
                    request.getUid(), request.getName(), request.getState(), request.getClientName());
        }

        BrokerPlayerManager playerManager = brokerServer.getBrokerPlayerManager();

        switch (request.getState()) {
            case JOIN -> {
                BrokerPlayer brokerPlayer = playerManager.getPlayer(request.getUid());
                if (brokerPlayer == null) {
                    return;
                }
                if (brokerPlayer.getBukkitServer() == null) {
                    brokerServer.getPluginManager().callEvent(new PlayerBukkitFirstJoinEvent(request.getClientName(), request.getUid(), request.getName()));
                }
                brokerPlayer.setBukkitServer(request.getClientName());
                asyncCtx.sendResponse(SUCCESS);
            }
            case CONNECT -> {
                BrokerPlayer brokerPlayer = new BrokerPlayer(brokerServer, request.getUid(), request.getName());
                brokerPlayer.setBungeeProxy(request.getClientName());
                BrokerPlayer player = playerManager.addPlayer(brokerPlayer);
                brokerServer.getPluginManager().callEvent(new PlayerBungeeLoginEvent(request.getUid(), request.getName()));
                if (player == null) {
                    asyncCtx.sendResponse(SUCCESS);
                } else {
                    asyncCtx.sendResponse(FAILED);
                }
            }
            case DISCONNECT -> {
                playerManager.removePlayer(request.getUid());
                brokerServer.getPluginManager().callEvent(new PlayerBungeeLogoutEvent(request.getUid(), request.getName()));
                asyncCtx.sendResponse(SUCCESS);
            }
        }
    }

    @Override
    public String interest() {
        return PlayerBungeeMessage.class.getName();
    }

}
