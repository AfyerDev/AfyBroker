package net.afyer.afybroker.bukkit;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import net.afyer.afybroker.bukkit.command.BroadcastChatCommand;
import net.afyer.afybroker.bukkit.processor.BroadcastChatBukkitProcessor;
import net.afyer.afybroker.bukkit.processor.SendPlayerChatBukkitProcessor;
import net.afyer.afybroker.bukkit.processor.SendPlayerTitleBukkitProcessor;
import net.afyer.afybroker.bukkit.processor.SudoBukkitProcessor;
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
                    .registerUserProcessor(new SendPlayerChatBukkitProcessor())
                    .registerUserProcessor(new BroadcastChatBukkitProcessor())
                    .registerUserProcessor(new SendPlayerTitleBukkitProcessor())
                    .registerUserProcessor(new SudoBukkitProcessor(this))
                    .build();

            brokerClient.startup();
            brokerClient.ping();
        } finally {
            Thread.currentThread().setContextClassLoader(oldLoader);
        }

        registerCommands();
    }

    @Override
    public void onDisable() {
        brokerClient.shutdown();
    }

    private void registerCommands() {
        new BroadcastChatCommand(this).register(this);
    }

    @Getter
    private static AfyBroker instance;
}
