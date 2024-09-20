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
import net.afyer.afybroker.core.MetadataKeys;
import net.afyer.afybroker.core.message.BrokerClientInfoMessage;
import net.afyer.afybroker.core.message.RequestBrokerClientInfoMessage;
import net.afyer.afybroker.core.message.RequestPlayerInfoMessage;
import net.afyer.afybroker.core.message.SyncServerMessage;
import net.afyer.afybroker.core.util.AbstractInvokeCallback;
import net.afyer.afybroker.server.BrokerServer;
import net.afyer.afybroker.server.aware.BrokerServerAware;
import net.afyer.afybroker.server.event.BrokerClientConnectEvent;
import net.afyer.afybroker.server.event.BrokerClientRegisterEvent;
import net.afyer.afybroker.server.processor.PlayerBukkitJoinBrokerProcessor;
import net.afyer.afybroker.server.processor.PlayerBungeeConnectBrokerProcessor;
import net.afyer.afybroker.server.proxy.BrokerClientProxy;
import net.afyer.afybroker.server.proxy.BrokerPlayer;

import java.util.*;
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
    final Executor connectionThread = Executors.newSingleThreadExecutor(new ThreadFactoryBuilder()
            .setNameFormat("Broker-Connection-Thread").build());

    @Override
    public void onEvent(String remoteAddress, Connection connection) {
        log.info("BrokerClient[{}] connected, sending request client info message", remoteAddress);

        BrokerClientConnectEvent event = new BrokerClientConnectEvent(remoteAddress, connection);
        brokerServer.getPluginManager().callEvent(event);

        InvokeCallback callback = registerBrokerClientCallback(remoteAddress);
        int timeoutMillis = BrokerGlobalConfig.DEFAULT_TIMEOUT_MILLIS;
        try {
            brokerServer.getRpcServer().invokeWithCallback(connection, requestBrokerClientInfoMessage, callback, timeoutMillis);
        } catch (RemotingException e) {
            log.info("Request client info to BrokerClient:{} failed", remoteAddress);
            log.error(e.getMessage(), e);
        }
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

                syncServer(brokerClientProxy);
                registerPlayer(brokerClientProxy);

                log.info("BrokerClient:{} registration successful", remoteAddress);
            }

            @Override
            public void onException(Throwable e) {
                log.error("BrokerClient:{} registration failed", remoteAddress);
                log.error(e.getMessage(), e);
            }

            @Override
            public Executor getExecutor() {
                return connectionThread;
            }
        };
    }

    private void registerPlayer(BrokerClientProxy clientProxy) {
        InvokeCallback callback = null;
        if (Objects.equals(clientProxy.getType(), BrokerClientType.PROXY)) {
            callback = registerPlayerBungeeCallback(clientProxy);
        } else if (Objects.equals(clientProxy.getType(), BrokerClientType.SERVER)) {
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
                return connectionThread;
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
                return connectionThread;
            }
        };
    }

    private void syncServer(BrokerClientProxy clientProxy) {
        // 如果是 mc 服务器则同步至所有 proxy 服务器
        if (clientProxy.getType().equals(BrokerClientType.SERVER)) {
            Map<String, String> servers = new HashMap<>();
            servers.put(clientProxy.getName(), clientProxy.getMetadata(MetadataKeys.MC_SERVER_ADDRESS));
            SyncServerMessage message = new SyncServerMessage()
                    .setServers(servers);
            List<BrokerClientProxy> proxyType = brokerServer.getBrokerClientProxyManager().getByType(BrokerClientType.PROXY);
            for (BrokerClientProxy proxy : proxyType) {
                try {
                    proxy.oneway(message);
                } catch (RemotingException | InterruptedException e) {
                    log.error(e.getMessage(), e);
                }
            }
        }
        // 如果是 proxy 服务器则发送当前连接的 mc 服务器
        if (clientProxy.getType().equals(BrokerClientType.PROXY)) {
            List<BrokerClientProxy> serverType = brokerServer.getBrokerClientProxyManager().getByType(BrokerClientType.SERVER);
            if (serverType.isEmpty()) return;
            Map<String, String> servers = new HashMap<>();
            for (BrokerClientProxy server : serverType) {
                servers.put(server.getName(), server.getMetadata(MetadataKeys.MC_SERVER_ADDRESS));
            }
            SyncServerMessage message = new SyncServerMessage()
                    .setServers(servers);
            try {
                clientProxy.oneway(message);
            } catch (RemotingException | InterruptedException e) {
                log.error(e.getMessage(), e);
            }
        }
    }

}
