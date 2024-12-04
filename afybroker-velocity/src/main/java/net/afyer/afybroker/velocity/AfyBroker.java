package net.afyer.afybroker.velocity;

import com.alipay.remoting.ConnectionEventType;
import com.alipay.remoting.exception.RemotingException;
import com.google.inject.Inject;
import com.velocitypowered.api.command.CommandManager;
import com.velocitypowered.api.event.PostOrder;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.event.proxy.ProxyShutdownEvent;
import com.velocitypowered.api.plugin.annotation.DataDirectory;
import com.velocitypowered.api.proxy.ProxyServer;
import lombok.Getter;
import net.afyer.afybroker.client.Broker;
import net.afyer.afybroker.client.BrokerClient;
import net.afyer.afybroker.client.BrokerClientBuilder;
import net.afyer.afybroker.core.BrokerClientType;
import net.afyer.afybroker.core.BrokerGlobalConfig;
import net.afyer.afybroker.core.util.BoltUtils;
import net.afyer.afybroker.velocity.listener.PlayerListener;
import net.afyer.afybroker.velocity.processor.*;
import net.afyer.afybroker.velocity.processor.connection.CloseEventVelocityProcessor;
import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.yaml.YAMLConfigurationLoader;
import org.slf4j.Logger;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;
import java.util.UUID;
import java.util.function.Consumer;

/**
 * @author Nipuru
 * @since 2022/7/28 7:26
 */
@Getter
public class AfyBroker {

    private final ProxyServer server;
    private final Logger logger;
    private final CommandManager commandManager;
    private final Path dataDirectory;
    private BrokerClient brokerClient;
    private ConfigurationNode config;
    private boolean syncEnable;

    @Inject
    public AfyBroker(
            ProxyServer server,
            Logger logger,
            CommandManager commandManager,
            @DataDirectory Path dataDirectory) {
        this.server = server;
        this.logger = logger;
        this.commandManager = commandManager;
        this.dataDirectory = dataDirectory;
    }

    @Subscribe(order = PostOrder.LAST)
    public void onProxyInitializeLast(ProxyInitializeEvent event) {
        ClassLoader oldLoader = Thread.currentThread().getContextClassLoader();
        try {
            Path configPath = dataDirectory.resolve("config.yml");
            if (Files.notExists(configPath)) {
                try (InputStream in = Objects.requireNonNull(getClass().getClassLoader().getResourceAsStream("config.yml"))) {
                    Files.createDirectories(configPath.getParent());
                    Files.copy(in, configPath);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
            try {
                config = YAMLConfigurationLoader.builder()
                        .setPath(configPath)
                        .build().load();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            syncEnable = config.getNode("server", "sync-enable").getBoolean(false);
            BrokerClientBuilder brokerClientBuilder = BrokerClient.newBuilder()
                    .host(config.getNode("broker", "host").getString(BrokerGlobalConfig.BROKER_HOST))
                    .port(config.getNode("broker", "port").getInt(BrokerGlobalConfig.BROKER_PORT))
                    .name(config.getNode("broker", "name").getString("velocity-%unique_id%")
                            .replace("%unique_id%", UUID.randomUUID().toString().substring(0, 8))
                            .replace("%hostname%", Objects.toString(System.getenv("HOSTNAME")))
                    )
                    .type(BrokerClientType.PROXY)
                    .registerUserProcessor(new ConnectToServerVelocityProcessor(this))
                    .registerUserProcessor(new KickPlayerVelocityProcessor(this))
                    .registerUserProcessor(new PlayerHeartbeatValidateVelocityProcessor(this))
                    .registerUserProcessor(new RequestPlayerInfoVelocityProcessor(this))
                    .registerUserProcessor(new SyncServerVelocityProcessor(this))
                    .registerUserProcessor(new PlayerProfilePropertyVelocityProcessor(this))
                    .addConnectionEventProcessor(ConnectionEventType.CLOSE, new CloseEventVelocityProcessor(this));
            for (Consumer<BrokerClientBuilder> buildAction : Broker.getBuildActions()) {
                buildAction.accept(brokerClientBuilder);
            }
            brokerClient = brokerClientBuilder.build();
            Broker.setClient(brokerClient);
            Thread.currentThread().setContextClassLoader(getClass().getClassLoader());
            BoltUtils.initProtocols();
            brokerClient.startup();
            brokerClient.ping();
        } catch (RemotingException | InterruptedException e) {
            logger.error("Broker client initialization failed!", e);
        } finally {
            Thread.currentThread().setContextClassLoader(oldLoader);
        }

        registerListeners();
    }

    private void registerListeners() {
        server.getEventManager().register(this, new PlayerListener(this));
    }

    @Subscribe(order = PostOrder.LAST)
    public void onProxyShutdownLast(ProxyShutdownEvent event) {
        brokerClient.shutdown();
        BoltUtils.clearProtocols();
    }
}
