package net.afyer.afybroker.bungee;

import com.alipay.remoting.ConnectionEventType;
import com.alipay.remoting.LifeCycleException;
import com.alipay.remoting.exception.RemotingException;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import net.afyer.afybroker.bungee.listener.PlayerListener;
import net.afyer.afybroker.bungee.processor.*;
import net.afyer.afybroker.bungee.processor.connection.CloseEventBungeeProcessor;
import net.afyer.afybroker.client.Broker;
import net.afyer.afybroker.client.BrokerClient;
import net.afyer.afybroker.client.BrokerClientBuilder;
import net.afyer.afybroker.core.BrokerClientType;
import net.afyer.afybroker.core.BrokerGlobalConfig;
import net.afyer.afybroker.core.util.BoltUtils;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.YamlConfiguration;
import org.bstats.bungeecord.Metrics;

import java.util.Objects;
import java.util.UUID;
import java.util.function.Consumer;

/**
 * @author Nipuru
 * @since 2022/7/28 7:26
 */
@Getter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AfyBroker extends Plugin {

    private BrokerClient brokerClient;
    private Configuration config;
    private boolean syncEnable;
    private Metrics metrics;

    @Override
    public void onEnable() {
        metrics = new Metrics(this, 26647);
        ClassLoader oldLoader = Thread.currentThread().getContextClassLoader();
        try {
            config = new BungeeFileConfig("config.yml", this, YamlConfiguration.class).get();
            syncEnable = config.getBoolean("server.sync-enable", false);
            BrokerClientBuilder brokerClientBuilder = BrokerClient.newBuilder()
                    .host(config.getString("broker.host", BrokerGlobalConfig.BROKER_HOST))
                    .port(config.getInt("broker.port", BrokerGlobalConfig.BROKER_PORT))
                    .name(config.getString("broker.name", "bungee-%unique_id%")
                            .replace("%unique_id%", UUID.randomUUID().toString().substring(0, 8))
                            .replace("%hostname%", Objects.toString(System.getenv("HOSTNAME")))
                    )
                    .addTags(getConfig().getStringList("tags"))
                    .type(BrokerClientType.PROXY)
                    .registerUserProcessor(new ConnectToServerBungeeProcessor())
                    .registerUserProcessor(new KickPlayerBungeeProcessor())
                    .registerUserProcessor(new PlayerHeartbeatValidateBungeeProcessor())
                    .registerUserProcessor(new RequestPlayerInfoBungeeProcessor())
                    .registerUserProcessor(new SyncServerBungeeProcessor(this))
                    .registerUserProcessor(new PlayerProfilePropertyBungeeProcessor())
                    .addConnectionEventProcessor(ConnectionEventType.CLOSE, new CloseEventBungeeProcessor(this));
            Configuration metadata = config.getSection("metadata");
            if (metadata != null) {
                for (String key : metadata.getKeys()) {
                    brokerClientBuilder.addMetadata(key, metadata.getString(key));
                }
            }
            for (Consumer<BrokerClientBuilder> buildAction : Broker.getBuildActions()) {
                buildAction.accept(brokerClientBuilder);
            }
            brokerClient = brokerClientBuilder.build();
            Broker.setClient(brokerClient);
            Thread.currentThread().setContextClassLoader(getClass().getClassLoader());
            BoltUtils.initProtocols();
            brokerClient.startup();
            brokerClient.ping();
        } catch (LifeCycleException e) {
            getLogger().severe("Broker client startup failed!");
            e.printStackTrace();
            getProxy().stop();
        } catch (RemotingException | InterruptedException e) {
            getLogger().severe("Ping to the broker server failed!");
            e.printStackTrace();
        } finally {
            Thread.currentThread().setContextClassLoader(oldLoader);
        }

        registerListeners();
    }

    @Override
    public void onDisable() {
        if (brokerClient != null) {
            brokerClient.shutdown();
        }
        BoltUtils.clearProtocols();
        if (metrics != null) {
            metrics.shutdown();
        }
    }

    private void registerListeners() {
        ProxyServer.getInstance().getPluginManager().registerListener(this, new PlayerListener(this));
    }
}
