package net.afyer.afybroker.server.processor;

import com.alipay.remoting.AsyncContext;
import com.alipay.remoting.BizContext;
import com.alipay.remoting.rpc.protocol.AsyncUserProcessor;
import net.afyer.afybroker.core.message.BrokerClientRegistrationResult;
import com.alipay.remoting.InvokeCallback;
import com.alipay.remoting.exception.RemotingException;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import net.afyer.afybroker.core.BrokerClientType;
import net.afyer.afybroker.core.MetadataKeys;
import net.afyer.afybroker.core.message.BrokerClientInfoMessage;
import net.afyer.afybroker.core.message.RequestPlayerInfoMessage;
import net.afyer.afybroker.core.message.SyncServerMessage;
import net.afyer.afybroker.core.util.AbstractInvokeCallback;
import net.afyer.afybroker.server.BrokerServer;
import net.afyer.afybroker.server.aware.BrokerServerAware;
import net.afyer.afybroker.server.event.ClientRegisterEvent;
import net.afyer.afybroker.server.proxy.BrokerClientItem;
import net.afyer.afybroker.server.proxy.BrokerPlayer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.ExecutorService;

/**
 * @author Nipuru
 * @since 2026/9/22 16:42
 */
public class RegisterClientBrokerProcessor extends AsyncUserProcessor<BrokerClientInfoMessage> implements BrokerServerAware {

    private static final Logger LOGGER = LoggerFactory.getLogger(RegisterClientBrokerProcessor.class);

    private BrokerServer brokerServer;
    private final RequestPlayerInfoMessage requestPlayerInfoMessage = new RequestPlayerInfoMessage();
    private final Map<UUID, String> playerBukkitMap = new HashMap<>();
    private final ExecutorService connectionThread = Executors.newSingleThreadExecutor(new ThreadFactoryBuilder()
            .setNameFormat("Broker-connection-thread").build());

    @Override
    public void setBrokerServer(BrokerServer brokerServer) {
        this.brokerServer = brokerServer;
    }

    @Override
    public void handleRequest(BizContext context, AsyncContext response, BrokerClientInfoMessage request) {
        if (context.isRequestTimeout()) {
            context.getConnection().close();
            return;
        }
        request.setAddress(context.getRemoteAddress());
        BrokerClientItem client;
        boolean added;
        try {
            client = new BrokerClientItem(request, context.getConnection(),
                    brokerServer.getRpcServer(), brokerServer.getInterceptors());
            added = brokerServer.getClientManager().register(client);
        } catch (IllegalArgumentException | NullPointerException e) {
            LOGGER.warn("BrokerClient registration rejected: {}: {}", context.getRemoteAddress(), e.getMessage());
            response.sendResponse(new BrokerClientRegistrationResult(e.getMessage() == null ? "Invalid client information" : e.getMessage()));
            return;
        }
        response.sendResponse(new BrokerClientRegistrationResult(null));
        if (added) {
            brokerServer.getPluginManager().callEvent(new ClientRegisterEvent(request, client));
            syncServer(client);
            registerPlayer(client);
            LOGGER.info("BrokerClient:{} registration successful", client.getName());
        }
    }

    @Override
    public String interest() {
        return BrokerClientInfoMessage.class.getName();
    }

    @Override
    public Executor getExecutor() {
        return connectionThread;
    }

    @Override
    public void shutdown() {
        connectionThread.shutdownNow();
        super.shutdown();
    }

    private void registerPlayer(BrokerClientItem client) {
        InvokeCallback callback = null;
        if (Objects.equals(client.getType(), BrokerClientType.PROXY)) {
            callback = registerPlayerBungeeCallback(client);
        } else if (Objects.equals(client.getType(), BrokerClientType.SERVER)) {
            callback = registerPlayerBukkitCallback(client);
        }
        if (callback == null) {
            return;
        }
        try {
            client.invokeWithCallback(requestPlayerInfoMessage, callback);
        } catch (RemotingException | InterruptedException e) {
            LOGGER.error("Request player server info to brokerClient:{} failed", client.getName());
            LOGGER.error(e.getMessage(), e);
        }
    }

    private InvokeCallback registerPlayerBungeeCallback(BrokerClientItem bungeeClient) {
        return new AbstractInvokeCallback() {
            @Override
            public void onResponse(Object result) {
                Map<UUID, String> playerMap = cast(result);
                playerMap.forEach((uuid, name) -> {
                    BrokerPlayer brokerPlayer = new BrokerPlayer(uuid, name, bungeeClient);
                    if (!PlayerProxyConnectBrokerProcessor.handlePlayerAdd(brokerServer, brokerPlayer)) {
                        return;
                    }
                    String bukkitAddress = playerBukkitMap.remove(uuid);
                    if (bukkitAddress == null) {
                        return;
                    }
                    BrokerClientItem bukkitClient = brokerServer.getClientManager().getByAddress(bukkitAddress);
                    if (bukkitClient == null) {
                        return;
                    }

                    PlayerServerJoinBrokerProcessor.handleBukkitJoin(brokerServer, brokerPlayer, bukkitClient);
                });
            }

            @Override
            public void onException(Throwable e) {
                LOGGER.error("Request player info to bungee brokerClient:{} failed", bungeeClient.getName());
                LOGGER.error(e.getMessage(), e);
            }

            @Override
            public Executor getExecutor() {
                return connectionThread;
            }
        };
    }

    private InvokeCallback registerPlayerBukkitCallback(BrokerClientItem bukkitClient) {
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
                    PlayerServerJoinBrokerProcessor.handleBukkitJoin(brokerServer, brokerPlayer, bukkitClient);
                });
            }

            @Override
            public void onException(Throwable e) {
                LOGGER.error("Request player info to bukkit brokerClient:{} failed", bukkitClient.getName());
                LOGGER.error(e.getMessage(), e);
            }

            @Override
            public Executor getExecutor() {
                return connectionThread;
            }
        };
    }

    private void syncServer(BrokerClientItem client) {
        // 如果是 mc 服务器则同步至所有 proxy 服务器
        if (client.getType().equals(BrokerClientType.SERVER)) {
            Map<String, String> servers = new HashMap<>();
            servers.put(client.getName(), client.getMetadata(MetadataKeys.MC_SERVER_ADDRESS));
            SyncServerMessage message = new SyncServerMessage().setServers(servers);
            List<BrokerClientItem> proxyType = brokerServer.getClientManager().getByType(BrokerClientType.PROXY);
            for (BrokerClientItem proxy : proxyType) {
                try {
                    proxy.oneway(message);
                } catch (RemotingException | InterruptedException e) {
                    LOGGER.error(e.getMessage(), e);
                }
            }
        }
        // 如果是 proxy 服务器则发送当前连接的 mc 服务器
        if (client.getType().equals(BrokerClientType.PROXY)) {
            List<BrokerClientItem> serverType = brokerServer.getClientManager().getByType(BrokerClientType.SERVER);
            if (serverType.isEmpty()) {
                return;
            }
            Map<String, String> servers = new HashMap<>();
            for (BrokerClientItem server : serverType) {
                servers.put(server.getName(), server.getMetadata(MetadataKeys.MC_SERVER_ADDRESS));
            }
            SyncServerMessage message = new SyncServerMessage().setServers(servers);
            try {
                client.oneway(message);
            } catch (RemotingException | InterruptedException e) {
                LOGGER.error(e.getMessage(), e);
            }
        }
    }

}
