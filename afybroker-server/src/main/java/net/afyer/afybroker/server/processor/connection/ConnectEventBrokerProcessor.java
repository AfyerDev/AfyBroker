package net.afyer.afybroker.server.processor.connection;

import com.alipay.remoting.Connection;
import com.alipay.remoting.ConnectionEventProcessor;
import com.alipay.remoting.InvokeCallback;
import com.alipay.remoting.exception.RemotingException;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import lombok.AccessLevel;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import net.afyer.afybroker.core.BrokerClientType;
import net.afyer.afybroker.core.BrokerGlobalConfig;
import net.afyer.afybroker.core.message.BrokerClientInfoMessage;
import net.afyer.afybroker.core.message.RequestBrokerClientInfoMessage;
import net.afyer.afybroker.core.message.RequestPlayerInfoMessage;
import net.afyer.afybroker.core.util.AbstractInvokeCallback;
import net.afyer.afybroker.server.BrokerServer;
import net.afyer.afybroker.server.aware.BrokerServerAware;
import net.afyer.afybroker.server.event.BrokerClientConnectEvent;
import net.afyer.afybroker.server.event.BrokerClientRegisterEvent;
import net.afyer.afybroker.server.processor.PlayerBukkitJoinBrokerProcessor;
import net.afyer.afybroker.server.processor.PlayerBungeeConnectBrokerProcessor;
import net.afyer.afybroker.server.proxy.BrokerClientProxy;
import net.afyer.afybroker.server.proxy.BrokerPlayer;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 * @author Nipuru
 * @since 2022/7/30 11:42
 */
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ConnectEventBrokerProcessor implements ConnectionEventProcessor, BrokerServerAware {

    @Setter
    BrokerServer brokerServer;

    final RequestBrokerClientInfoMessage requestBrokerClientInfoMessage = new RequestBrokerClientInfoMessage();
    final RequestPlayerInfoMessage requestPlayerInfoMessage = new RequestPlayerInfoMessage();
    final Map<UUID, String> playerBukkitMap = new HashMap<>(); // single thread access
    final Executor playerRegistrationThread = Executors.newSingleThreadExecutor(new ThreadFactoryBuilder()
            .setNameFormat("Player-Registration-Thread").build());

    @Override
    public void onEvent(String remoteAddress, Connection connection) {
        log.info("BrokerClient[{}] connected, sending request client info message", remoteAddress);

        InvokeCallback callback = registerBrokerClientCallback(remoteAddress);
        int timeoutMillis = BrokerGlobalConfig.DEFAULT_TIMEOUT_MILLIS;
        try {
            brokerServer.getRpcServer().invokeWithCallback(connection, requestBrokerClientInfoMessage, callback, timeoutMillis);
        } catch (RemotingException e) {
            log.info("Request client info to BrokerClient:{} failed", remoteAddress);
            log.error(e.getMessage(), e);
        }

        BrokerClientConnectEvent event = new BrokerClientConnectEvent(remoteAddress, connection);
        brokerServer.getPluginManager().callEvent(event);
    }

    private InvokeCallback registerBrokerClientCallback(String remoteAddress) {
        return new AbstractInvokeCallback() {
            @Override
            public void onResponse(Object result) {
                BrokerClientInfoMessage clientInfoMessage = cast(result);
                clientInfoMessage.setAddress(remoteAddress);

                BrokerClientProxy brokerClientProxy = new BrokerClientProxy(clientInfoMessage, brokerServer.getRpcServer());
                brokerServer.getBrokerClientProxyManager().register(brokerClientProxy);

                BrokerClientRegisterEvent event = new BrokerClientRegisterEvent(clientInfoMessage, brokerClientProxy);
                brokerServer.getPluginManager().callEvent(event);

                registerPlayer(brokerClientProxy);

                log.info("BrokerClient:{} registration successful", remoteAddress);
            }

            @Override
            public void onException(Throwable e) {
                log.error("BrokerClient:{} registration failed", remoteAddress);
                log.error(e.getMessage(), e);
            }
        };
    }

    private void registerPlayer(BrokerClientProxy clientProxy) {
        InvokeCallback callback = null;
        if (clientProxy.getType() == BrokerClientType.BUNGEE) {
            callback = registerPlayerBungeeCallback(clientProxy);
        } else if (clientProxy.getType() == BrokerClientType.BUKKIT) {
            callback = registerPlayerBukkitCallback(clientProxy);
        }
        if (callback == null) return;
        try {
            clientProxy.invokeWithCallback(requestPlayerInfoMessage, callback);
        } catch (RemotingException | InterruptedException e) {
            log.error("Request player server info to brokerClient:{} failed", clientProxy.getName());
            log.error(e.getMessage(), e);
        }
    }

    private InvokeCallback registerPlayerBungeeCallback(BrokerClientProxy bungeeClient) {
        return new AbstractInvokeCallback() {
            @Override
            public void onResponse(Object result) {
                Map<UUID, String> playerMap = cast(result);
                playerMap.forEach((uuid, name) -> {
                    BrokerPlayer brokerPlayer = new BrokerPlayer(uuid, name, bungeeClient);
                    if (!PlayerBungeeConnectBrokerProcessor.handlePlayerAdd(brokerServer, brokerPlayer)) return;
                    String bukkitAddress = playerBukkitMap.remove(uuid);
                    if (bukkitAddress == null) return;
                    BrokerClientProxy bukkitClient = brokerServer.getBrokerClientProxyManager().getByAddress(bukkitAddress);
                    if (bukkitClient == null) return;

                    PlayerBukkitJoinBrokerProcessor.handleBukkitJoin(brokerServer, brokerPlayer, bukkitClient);
                });
            }

            @Override
            public void onException(Throwable e) {
                log.error("Request player info to bungee brokerClient:{} failed", bungeeClient.getName());
                log.error(e.getMessage(), e);
            }

            @Override
            public Executor getExecutor() {
                return playerRegistrationThread;
            }
        };
    }

    private InvokeCallback registerPlayerBukkitCallback(BrokerClientProxy bukkitClient) {
        return new AbstractInvokeCallback() {
            @Override
            public void onResponse(Object result) {
                List<UUID> playerList = cast(result);
                playerList.forEach((uuid) -> {
                    BrokerPlayer brokerPlayer = brokerServer.getPlayer(uuid);
                    if (brokerPlayer == null) {
                        playerBukkitMap.put(uuid, bukkitClient.getAddress());
                        return;
                    }
                    PlayerBukkitJoinBrokerProcessor.handleBukkitJoin(brokerServer, brokerPlayer, bukkitClient);
                });
            }

            @Override
            public void onException(Throwable e) {
                log.error("Request player info to bukkit brokerClient:{} failed", bukkitClient.getName());
                log.error(e.getMessage(), e);
            }

            @Override
            public Executor getExecutor() {
                return playerRegistrationThread;
            }
        };
    }

}
