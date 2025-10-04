package net.afyer.afybroker.bukkit;

import com.alipay.remoting.LifeCycleException;
import com.alipay.remoting.exception.RemotingException;
import net.afyer.afybroker.bukkit.listener.PlayerListener;
import net.afyer.afybroker.bukkit.preprocessor.BukkitServerThreadPreprocessor;
import net.afyer.afybroker.bukkit.processor.BroadcastChatBukkitProcessor;
import net.afyer.afybroker.bukkit.processor.RequestPlayerInfoBukkitProcessor;
import net.afyer.afybroker.bukkit.processor.SendPlayerChatBukkitProcessor;
import net.afyer.afybroker.bukkit.processor.SendPlayerTitleBukkitProcessor;
import net.afyer.afybroker.client.Broker;
import net.afyer.afybroker.client.BrokerClient;
import net.afyer.afybroker.client.BrokerClientBuilder;
import net.afyer.afybroker.client.processor.CloseBrokerClientProcessor;
import net.afyer.afybroker.core.BrokerClientType;
import net.afyer.afybroker.core.BrokerGlobalConfig;
import net.afyer.afybroker.core.MetadataKeys;
import net.afyer.afybroker.core.util.BoltUtils;
import net.afyer.afybroker.core.util.LoggerAdapter;
import org.bstats.bukkit.Metrics;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Objects;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.logging.Level;

/**
 * @author Nipuru
 * @since 2022/7/28 7:26
 */
public class AfyBroker extends JavaPlugin {
    private BrokerClient brokerClient;
    private Metrics metrics;

    @Override
    public void onEnable() {
        saveDefaultConfig();
        metrics = new Metrics(this, 26646);
        try {
            String serverAddress = String.format("%s:%s",
                    getConfig().getString("server.host", getDefaultServerIp()),
                    getConfig().getInt("server.port", Bukkit.getPort()));
            BrokerClientBuilder builder = BrokerClient.newBuilder()
                    .host(getConfig().getString("broker.host", BrokerGlobalConfig.BROKER_HOST))
                    .port(getConfig().getInt("broker.port", BrokerGlobalConfig.BROKER_PORT))
                    .name(getConfig().getString("broker.name", "bukkit-%unique_id%")
                            .replace("%unique_id%", UUID.randomUUID().toString().substring(0, 8))
                            .replace("%hostname%", Objects.toString(System.getenv("HOSTNAME"))))
                    .addTags(getConfig().getStringList("tags"))
                    .addMetadata(MetadataKeys.MC_SERVER_ADDRESS, serverAddress)
                    .type(BrokerClientType.SERVER)
                    .registerUserProcessor(new SendPlayerChatBukkitProcessor())
                    .registerUserProcessor(new BroadcastChatBukkitProcessor())
                    .registerUserProcessor(new SendPlayerTitleBukkitProcessor())
                    .registerUserProcessor(new RequestPlayerInfoBukkitProcessor())
                    .registerUserProcessor(new CloseBrokerClientProcessor(Bukkit::shutdown))
                    .registerPreprocessor(new BukkitServerThreadPreprocessor(
                            getConfig().getBoolean("server.thread-check", true)));
            ConfigurationSection metadata = getConfig().getConfigurationSection("metadata");
            if (metadata != null) {
                for (String key : metadata.getKeys(false)) {
                    builder.addMetadata(key, metadata.getString(key));
                }
            }
            for (Consumer<BrokerClientBuilder> buildAction : Broker.getBuildActions()) {
                buildAction.accept(builder);
            }
            brokerClient = builder.build();
            Broker.setClient(brokerClient);
            BoltUtils.initProtocols();
            brokerClient.startup();
            brokerClient.printInformation(LoggerAdapter.toSlf4j(getLogger()));
            brokerClient.ping();
        } catch (LifeCycleException e) {
            getLogger().log(Level.SEVERE, "Broker client startup failed!", e);
            Bukkit.shutdown();
        } catch (RemotingException | InterruptedException e) {
            getLogger().log(Level.SEVERE,"Ping to the broker server failed!", e);
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

    private void registerListeners() {
        getServer().getPluginManager().registerEvents(new PlayerListener(this), this);
    }

    private static String getDefaultServerIp() {
        String ip = Bukkit.getIp();
        if (ip.isEmpty()) {
            return "localhost";
        }
        return ip;
    }

}
