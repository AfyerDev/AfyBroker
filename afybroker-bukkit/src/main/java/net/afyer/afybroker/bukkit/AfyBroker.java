package net.afyer.afybroker.bukkit;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import net.afyer.afybroker.bukkit.command.BroadcastChatCommand;
import net.afyer.afybroker.bukkit.listener.PlayerListener;
import net.afyer.afybroker.bukkit.processor.*;
import net.afyer.afybroker.client.Broker;
import net.afyer.afybroker.client.BrokerClient;
import net.afyer.afybroker.core.BrokerClientType;
import net.afyer.afybroker.core.BrokerGlobalConfig;
import net.afyer.afybroker.core.util.BoltUtils;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.UUID;

/**
 * @author Nipuru
 * @since 2022/7/28 7:26
 */
@Getter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AfyBroker extends JavaPlugin {

    private BrokerClient brokerClient;

    @Override
    public void onEnable() {
        saveDefaultConfig();

        ClassLoader oldLoader = Thread.currentThread().getContextClassLoader();
        try {
            Thread.currentThread().setContextClassLoader(getClassLoader());

            brokerClient = BrokerClient.newBuilder()
                    .host(getConfig().getString("broker.host", BrokerGlobalConfig.BROKER_HOST))
                    .port(getConfig().getInt("broker.port", BrokerGlobalConfig.BROKER_PORT))
                    .name(getConfig().getString("broker.name", "bukkit-%unique_id%").replace("%unique_id%", UUID.randomUUID().toString().substring(0, 8)))
                    .addTags(getConfig().getStringList("broker.tags"))
                    .type(BrokerClientType.BUKKIT)
                    .registerUserProcessor(new SendPlayerChatBukkitProcessor())
                    .registerUserProcessor(new BroadcastChatBukkitProcessor())
                    .registerUserProcessor(new SendPlayerTitleBukkitProcessor())
                    .registerUserProcessor(new SudoBukkitProcessor(this))
                    .registerUserProcessor(new KickPlayerBukkitProcessor())
                    .build();

            Broker.setClient(brokerClient);
            BoltUtils.ensureRegistered();

            brokerClient.startup();
            brokerClient.ping();
        } catch (Exception e) {
            getLogger().severe("Broker client initialization failed!");
            e.printStackTrace();
            getServer().shutdown();
        } finally {
            Thread.currentThread().setContextClassLoader(oldLoader);
        }

        registerCommands();
        registerListeners();
    }

    @Override
    public void onDisable() {
        brokerClient.shutdown();
        BoltUtils.ensureUnregistered();
    }

    private void registerCommands() {
        new BroadcastChatCommand(this).register(this);
    }

    private void registerListeners() {
        new PlayerListener(this).register(this);
    }

}
