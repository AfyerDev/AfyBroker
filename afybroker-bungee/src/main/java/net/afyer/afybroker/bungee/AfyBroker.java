package net.afyer.afybroker.bungee;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import net.afyer.afybroker.bungee.listener.PlayerListener;
import net.afyer.afybroker.bungee.processor.*;
import net.afyer.afybroker.client.Broker;
import net.afyer.afybroker.client.BrokerClient;
import net.afyer.afybroker.core.BrokerClientType;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.YamlConfiguration;

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
                    .name(config.getString("broker.name"))
                    .type(BrokerClientType.BUNGEE)
                    .addTags(config.getStringList("broker.tags"))
                    .registerUserProcessor(new SendPlayerChatBungeeProcessor())
                    .registerUserProcessor(new BroadcastChatBungeeProcessor())
                    .registerUserProcessor(new SudoBungeeProcessor())
                    .registerUserProcessor(new ConnectToServerBungeeProcessor())
                    .registerUserProcessor(new KickPlayerBungeeProcessor())
                    .build();

            brokerClient.startup();
            brokerClient.ping();

        } finally {
            Thread.currentThread().setContextClassLoader(oldLoader);
        }

        Broker.setClient(brokerClient);

        new PlayerListener(this).register(this);
    }

    @Override
    public void onDisable() {
        brokerClient.shutdown();
    }
}
