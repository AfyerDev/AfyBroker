package net.afyer.afybroker.bungee;

import com.alipay.remoting.exception.RemotingException;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import net.afyer.afybroker.bungee.listener.PlayerListener;
import net.afyer.afybroker.bungee.processor.*;
import net.afyer.afybroker.client.Broker;
import net.afyer.afybroker.client.BrokerClient;
import net.afyer.afybroker.core.BrokerClientType;
import net.afyer.afybroker.core.BrokerGlobalConfig;
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

            brokerClient = BrokerClient.newBuilder()
                    .host(config.getString("broker.host", BrokerGlobalConfig.BROKER_HOST))
                    .port(config.getInt("broker.port", BrokerGlobalConfig.BROKER_PORT))
                    .name(config.getString("broker.name", "bungee-%unique_id%").replace("%unique_id%", UUID.randomUUID().toString().substring(0, 8)))
                    .type(BrokerClientType.BUNGEE)
                    .addTags(config.getStringList("broker.tags"))
                    .registerUserProcessor(new SendPlayerChatBungeeProcessor())
                    .registerUserProcessor(new BroadcastChatBungeeProcessor())
                    .registerUserProcessor(new SudoBungeeProcessor())
                    .registerUserProcessor(new ConnectToServerBungeeProcessor())
                    .registerUserProcessor(new KickPlayerBungeeProcessor())
                    .build();

            Broker.setClient(brokerClient);

            brokerClient.startup();
            brokerClient.ping();

        } catch (RemotingException | InterruptedException e) {
            getLogger().severe("Broker client initialization failed!");
            e.printStackTrace();
            getProxy().stop();
        } finally {
            Thread.currentThread().setContextClassLoader(oldLoader);
        }

        getProxy().getPluginManager().registerListener(this, new PlayerListener(this));
    }

    @Override
    public void onDisable() {
        brokerClient.shutdown();
    }
}
