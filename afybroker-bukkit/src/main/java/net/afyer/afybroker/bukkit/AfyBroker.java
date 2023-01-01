package net.afyer.afybroker.bukkit;

import com.alipay.remoting.exception.RemotingException;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import net.afyer.afybroker.bukkit.command.BroadcastChatCommand;
import net.afyer.afybroker.bukkit.processor.*;
import net.afyer.afybroker.client.Broker;
import net.afyer.afybroker.client.BrokerClient;
import net.afyer.afybroker.core.BrokerClientType;
import net.afyer.afybroker.core.BrokerGlobalConfig;
import net.afyer.afybroker.core.message.ServerReadyToCloseMessage;
import org.bukkit.Bukkit;
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
                    .name(getConfig().getString("broker.name", "bukkit-" + UUID.randomUUID().toString().substring(0, 8)))
                    .addTags(getConfig().getStringList("broker.tags"))
                    .type(BrokerClientType.BUKKIT)
                    .registerUserProcessor(new SendPlayerChatBukkitProcessor())
                    .registerUserProcessor(new BroadcastChatBukkitProcessor())
                    .registerUserProcessor(new SendPlayerTitleBukkitProcessor())
                    .registerUserProcessor(new SudoBukkitProcessor(this))
                    .registerUserProcessor(new KickPlayerBukkitProcessor())
                    .build();

            Broker.setClient(brokerClient);

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
    }

    @Override
    public void onDisable() {
        try {
            brokerClient.invokeSync(new ServerReadyToCloseMessage()
                    .setServer(brokerClient.getClientInfo().getName())
                    .setTags(brokerClient.getClientInfo().getTags()));
        } catch (RemotingException | InterruptedException e) {
            e.printStackTrace();
        }
        brokerClient.shutdown();
    }

    private void registerCommands() {
        new BroadcastChatCommand(this).register(this);
    }

}
