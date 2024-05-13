package net.afyer.afybroker.bungee;

import com.alipay.remoting.ConnectionEventType;
import com.alipay.remoting.exception.RemotingException;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import net.afyer.afybroker.bungee.listener.PlayerListener;
import net.afyer.afybroker.bungee.processor.ConnectToServerBungeeProcessor;
import net.afyer.afybroker.bungee.processor.KickPlayerBungeeProcessor;
import net.afyer.afybroker.bungee.processor.PlayerHeartbeatValidateBungeeProcessor;
import net.afyer.afybroker.bungee.processor.SudoBungeeProcessor;
import net.afyer.afybroker.bungee.processor.connection.CloseEventBungeeProcessor;
import net.afyer.afybroker.client.Broker;
import net.afyer.afybroker.client.BrokerClient;
import net.afyer.afybroker.core.BrokerClientType;
import net.afyer.afybroker.core.BrokerGlobalConfig;
import net.afyer.afybroker.core.util.BoltUtils;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.YamlConfiguration;

import java.util.UUID;

/**
 * @author Nipuru
 * @since 2022/7/28 7:26
 */
@Getter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AfyBroker extends Plugin {

    private BrokerClient brokerClient;

    @Override
    public void onEnable() {
        Configuration config = new BungeeFileConfig("config.yml", this, YamlConfiguration.class).get();

        ClassLoader oldLoader = Thread.currentThread().getContextClassLoader();
        try {
            Thread.currentThread().setContextClassLoader(getClass().getClassLoader());

            String hostname = System.getenv("HOSTNAME");
            if (hostname == null) {
                hostname = "";
            }
            brokerClient = BrokerClient.newBuilder()
                    .host(config.getString("broker.host", BrokerGlobalConfig.BROKER_HOST))
                    .port(config.getInt("broker.port", BrokerGlobalConfig.BROKER_PORT))
                    .name(config.getString("broker.name", "bungee-%unique_id%")
                            .replace("%unique_id%", UUID.randomUUID().toString().substring(0, 8))
                            .replace("%hostname%", hostname)
                    )
                    .type(BrokerClientType.BUNGEE)
                    .addTags(config.getStringList("broker.tags"))
                    .registerUserProcessor(new SudoBungeeProcessor())
                    .registerUserProcessor(new ConnectToServerBungeeProcessor())
                    .registerUserProcessor(new KickPlayerBungeeProcessor())
                    .registerUserProcessor(new PlayerHeartbeatValidateBungeeProcessor())
                    .addConnectionEventProcessor(ConnectionEventType.CLOSE, new CloseEventBungeeProcessor())
                    .build();

            Broker.setClient(brokerClient);
            BoltUtils.ensureRegistered();

            brokerClient.startup();
            brokerClient.ping();

        } catch (RemotingException | InterruptedException e) {
            getLogger().severe("Broker client initialization failed!");
            e.printStackTrace();
            getProxy().stop();
        } finally {
            Thread.currentThread().setContextClassLoader(oldLoader);
        }

        registerListeners();
    }

    @Override
    public void onDisable() {
        brokerClient.shutdown();
        BoltUtils.ensureUnregistered();
    }

    private void registerListeners() {
        new PlayerListener(this).register(this);
    }
}
