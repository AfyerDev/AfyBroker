package net.afyer.afybroker.bukkit;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import net.afyer.afybroker.bukkit.command.ConnectCommand;
import net.afyer.afybroker.bukkit.listener.PlayerListener;
import net.afyer.afybroker.bukkit.processor.BroadcastChatBukkitProcessor;
import net.afyer.afybroker.bukkit.processor.PlayerConnectOtherBukkitProcessor;
import net.afyer.afybroker.bukkit.processor.SendPlayerChatBukkitProcessor;
import net.afyer.afybroker.client.BrokerClient;
import net.afyer.afybroker.core.BrokerClientType;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * @author Nipuru
 * @since 2022/7/28 7:26
 */
@Getter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AfyBroker extends JavaPlugin {

    BrokerClient brokerClient;

    @Override
    public void onEnable() {
        instance = this;
        saveDefaultConfig();

        getServer().getMessenger().registerOutgoingPluginChannel(this, "BungeeCord");

        ClassLoader oldLoader = Thread.currentThread().getContextClassLoader();
        try {
            Thread.currentThread().setContextClassLoader(getClassLoader());

            brokerClient = BrokerClient.newBuilder()
                    .name(getConfig().getString("broker.name"))
                    .tag(getConfig().getString("broker.tag"))
                    .type(BrokerClientType.BUKKIT)
                    .registerUserProcessor(new PlayerConnectOtherBukkitProcessor(this))
                    .registerUserProcessor(new SendPlayerChatBukkitProcessor())
                    .registerUserProcessor(new BroadcastChatBukkitProcessor())
                    .build();

            brokerClient.startup();
            brokerClient.ping();
        } finally {
            Thread.currentThread().setContextClassLoader(oldLoader);
        }

        registerListeners();
        registerCommands();
    }

    @Override
    public void onDisable() {
        brokerClient.shutdown();
    }

    private void registerListeners() {
        new PlayerListener(this).register(this);
    }

    private void registerCommands() {
        new ConnectCommand().register(this);
    }

    @Getter
    private static AfyBroker instance;
}
