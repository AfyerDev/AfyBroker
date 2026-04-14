package net.afyer.afybroker.bungee;

import com.alipay.remoting.ConnectionEventType;
import com.alipay.remoting.LifeCycleException;
import com.alipay.remoting.exception.RemotingException;
import net.afyer.afybroker.bungee.listener.PlayerListener;
import net.afyer.afybroker.bungee.processor.ConnectToServerBungeeProcessor;
import net.afyer.afybroker.bungee.processor.KickPlayerBungeeProcessor;
import net.afyer.afybroker.bungee.processor.PlayerHeartbeatValidateBungeeProcessor;
import net.afyer.afybroker.bungee.processor.PlayerProfilePropertyBungeeProcessor;
import net.afyer.afybroker.bungee.processor.RequestPlayerInfoBungeeProcessor;
import net.afyer.afybroker.bungee.processor.SyncServerBungeeProcessor;
import net.afyer.afybroker.bungee.processor.connection.CloseEventBungeeProcessor;
import net.afyer.afybroker.client.Broker;
import net.afyer.afybroker.client.BrokerClient;
import net.afyer.afybroker.client.BrokerClientBuilder;
import net.afyer.afybroker.client.processor.CloseBrokerClientProcessor;
import net.afyer.afybroker.core.BrokerClientType;
import net.afyer.afybroker.core.BrokerGlobalConfig;
import net.afyer.afybroker.core.Bstats;
import net.afyer.afybroker.core.observability.PlayerEventType;
import net.afyer.afybroker.core.observability.PrometheusObservabilityOptions;
import net.afyer.afybroker.core.util.BoltUtils;
import net.afyer.afybroker.core.util.LoggerAdapter;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.YamlConfiguration;
import org.bstats.bungeecord.Metrics;

import java.util.Objects;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.logging.Level;

public class AfyBroker extends Plugin {
    private BrokerClient brokerClient;
    private Configuration config;
    private boolean syncEnable;
    private Metrics metrics;

    @Override
    public void onEnable() {
        metrics = new Metrics(this, Bstats.BUNGEE);
        try {
            config = new BungeeFileConfig("config.yml", this, YamlConfiguration.class).get();
            syncEnable = config.getBoolean("server.sync-enable", false);
            BrokerClientBuilder brokerClientBuilder = BrokerClient.newBuilder()
                    .host(config.getString("broker.host", BrokerGlobalConfig.BROKER_HOST))
                    .port(config.getInt("broker.port", BrokerGlobalConfig.BROKER_PORT))
                    .name(config.getString("broker.name", "bungee-%unique_id%")
                            .replace("%unique_id%", UUID.randomUUID().toString().substring(0, 8))
                            .replace("%hostname%", Objects.toString(System.getenv("HOSTNAME"))))
                    .addTags(getConfig().getStringList("tags"))
                    .type(BrokerClientType.PROXY)
                    .registerUserProcessor(new ConnectToServerBungeeProcessor())
                    .registerUserProcessor(new KickPlayerBungeeProcessor())
                    .registerUserProcessor(new PlayerHeartbeatValidateBungeeProcessor())
                    .registerUserProcessor(new RequestPlayerInfoBungeeProcessor())
                    .registerUserProcessor(new SyncServerBungeeProcessor(this))
                    .registerUserProcessor(new PlayerProfilePropertyBungeeProcessor())
                    .registerUserProcessor(new CloseBrokerClientProcessor(getProxy()::stop))
                    .addConnectionEventProcessor(ConnectionEventType.CLOSE, new CloseEventBungeeProcessor(this));
            if (config.getBoolean("observability.prometheus.enabled", false)) {
                brokerClientBuilder.enablePrometheus(new PrometheusObservabilityOptions()
                        .setHost(config.getString("observability.prometheus.host", "0.0.0.0"))
                        .setPort(config.getInt("observability.prometheus.port", 9464)));
            }
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
            BoltUtils.initProtocols();
            brokerClient.startup();
            brokerClient.printInformation(LoggerAdapter.toSlf4j(getLogger()));
            brokerClient.ping();
            brokerClient.recordOnlinePlayers(ProxyServer.getInstance().getOnlineCount());
        } catch (LifeCycleException e) {
            getLogger().log(Level.SEVERE, "Broker client startup failed!", e);
            getProxy().stop();
        } catch (RemotingException | InterruptedException e) {
            getLogger().log(Level.SEVERE, "Ping to the broker server failed!", e);
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

    public BrokerClient getBrokerClient() {
        return brokerClient;
    }

    public Configuration getConfig() {
        return config;
    }

    public boolean isSyncEnable() {
        return syncEnable;
    }

    public void recordPlayerJoin() {
        if (brokerClient != null) {
            brokerClient.recordPlayerEvent(PlayerEventType.JOIN, ProxyServer.getInstance().getOnlineCount());
        }
    }

    public void recordPlayerLeave() {
        if (brokerClient != null) {
            brokerClient.recordPlayerEvent(PlayerEventType.LEAVE, ProxyServer.getInstance().getOnlineCount());
        }
    }

    private void registerListeners() {
        ProxyServer.getInstance().getPluginManager().registerListener(this, new PlayerListener(this));
    }
}
