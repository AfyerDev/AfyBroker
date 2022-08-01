package net.afyer.afybroker.server.processor;

import com.alipay.remoting.AsyncContext;
import com.alipay.remoting.BizContext;
import com.alipay.remoting.rpc.protocol.AsyncUserProcessor;
import lombok.AccessLevel;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import net.afyer.afybroker.core.BrokerClientType;
import net.afyer.afybroker.core.BrokerGlobalConfig;
import net.afyer.afybroker.core.message.BrokerPlayerBungeeMessage;
import net.afyer.afybroker.server.BrokerServer;
import net.afyer.afybroker.server.aware.BrokerServerAware;
import net.afyer.afybroker.server.proxy.BrokerClientProxy;
import net.afyer.afybroker.server.proxy.BrokerClientProxyManager;
import net.afyer.afybroker.server.proxy.BrokerPlayer;
import net.afyer.afybroker.server.proxy.BrokerPlayerManager;

import java.util.concurrent.Executor;

/**
 * @author Nipuru
 * @since 2022/8/1 11:41
 */
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE)
public class BrokerPlayerBungeeProcessor extends AsyncUserProcessor<BrokerPlayerBungeeMessage> implements BrokerServerAware {

    @Setter
    BrokerServer brokerServer;

    @Override
    public void handleRequest(BizContext bizCtx, AsyncContext asyncCtx, BrokerPlayerBungeeMessage request) {

        if (BrokerGlobalConfig.openLog) {
            log.info("Received player bungee message {}", request);
        }

        BrokerPlayerManager playerManager = brokerServer.getBrokerPlayerManager();
        BrokerClientProxyManager clientProxyManager = brokerServer.getBrokerClientProxyManager();

        switch (request.getState()) {
            case BUNGEE -> {
                if (request.getData() == null) return;

                BrokerPlayer brokerPlayer = playerManager.getPlayer(request.getUid());
                if (brokerPlayer == null) {
                    brokerPlayer = new BrokerPlayer(brokerServer, request.getUid());
                    BrokerClientProxy clientProxy = clientProxyManager.getByAddress(bizCtx.getRemoteAddress());

                    if (clientProxy == null) return;
                    if (clientProxy.getType() != BrokerClientType.BUNGEE) return;

                    brokerPlayer.setBungeeProxy(clientProxy.getName());
                    brokerPlayer.setBukkitServer(request.getData());
                    playerManager.addPlayer(brokerPlayer);
                    return;
                }
                brokerPlayer.setBukkitServer(request.getData());
            }
            case CONNECT -> {
                if (request.getData() == null) return;
                BrokerPlayer brokerPlayer = new BrokerPlayer(brokerServer, request.getUid());
                brokerPlayer.setBungeeProxy(request.getData());
                playerManager.addPlayer(brokerPlayer);
            }
            case DISCONNECT -> playerManager.removePlayer(request.getUid());
        }
    }

    @Override
    public String interest() {
        return BrokerPlayerBungeeMessage.class.getName();
    }

    @Override
    public Executor getExecutor() {
        return brokerServer.getBizThread();
    }
}
