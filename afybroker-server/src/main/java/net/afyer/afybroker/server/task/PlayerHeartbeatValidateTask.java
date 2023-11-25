package net.afyer.afybroker.server.task;

import com.alipay.remoting.exception.RemotingException;
import lombok.extern.slf4j.Slf4j;
import net.afyer.afybroker.core.message.PlayerHeartbeatValidateMessage;
import net.afyer.afybroker.core.util.AbstractInvokeCallback;
import net.afyer.afybroker.server.BrokerServer;
import net.afyer.afybroker.server.event.PlayerBungeeLogoutEvent;
import net.afyer.afybroker.server.proxy.BrokerClientProxy;
import net.afyer.afybroker.server.proxy.BrokerPlayer;
import net.afyer.afybroker.server.proxy.BrokerPlayerManager;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * 玩家心跳验证任务
 *
 * @author Nipuru
 * @since 2023/11/25 12:37
 */
@Slf4j
public class PlayerHeartbeatValidateTask extends Thread {

    private static final long period = TimeUnit.SECONDS.toMillis(5L);

    private final AtomicBoolean running = new AtomicBoolean(true);
    private final BrokerServer brokerServer;

    public PlayerHeartbeatValidateTask(BrokerServer brokerServer) {
        this.brokerServer = brokerServer;
    }

    public void cancel() {
        this.running.set(false);
    }

    @Override
    public void run() {
        while (running.get()) {
            try {
                validate();
            } catch (Throwable t) {
                log.error("PlayerHeartbeatValidateTask encountered an exception", t);
            }

            if (period <= 0) {
                break;
            }

            try {
                Thread.sleep(period);
            } catch (InterruptedException ex) {
                Thread.currentThread().interrupt();
            }
        }
    }

    private void validate() {
        BrokerPlayerManager playerManager = brokerServer.getBrokerPlayerManager();
        Collection<BrokerPlayer> brokerPlayers = playerManager.getPlayers();
        if (brokerPlayers.isEmpty()) return;
        Map<BrokerClientProxy, List<UUID>> map = new IdentityHashMap<>();
        for (BrokerPlayer player : brokerPlayers) {
            BrokerClientProxy bungeeProxy = player.getBungeeClientProxy();
            map.computeIfAbsent(bungeeProxy, k -> new ArrayList<>())
                    .add(player.getUid());
        }
        for (Map.Entry<BrokerClientProxy, List<UUID>> entry : map.entrySet()) {
            PlayerHeartbeatValidateMessage message = new PlayerHeartbeatValidateMessage()
                    .setUniqueIdList(entry.getValue());
            BrokerClientProxy bungeeProxy = entry.getKey();
            try {
                bungeeProxy.invokeWithCallback(message, new AbstractInvokeCallback() {
                    @Override
                    public void onResponse(Object result) {
                        List<UUID> response = (List<UUID>) result;
                        if (response.isEmpty()) return;
                        for (UUID uniqueId : response) {
                            BrokerPlayer brokerPlayer = playerManager.getPlayer(uniqueId);
                            if (brokerPlayer != null) {
                                brokerServer.getPluginManager().callEvent(new PlayerBungeeLogoutEvent(brokerPlayer));
                                playerManager.removePlayer(uniqueId);
                            }
                        }
                    }
                });
            } catch (RemotingException | InterruptedException ignored) {
            }
        }
    }
}
