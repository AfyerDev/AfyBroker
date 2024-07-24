package net.afyer.afybroker.bukkit;

import com.alipay.remoting.LifeCycleException;
import com.alipay.remoting.exception.RemotingException;
import lombok.Getter;
import net.afyer.afybroker.bukkit.listener.PlayerListener;
import net.afyer.afybroker.bukkit.processor.*;
import net.afyer.afybroker.client.Broker;
import net.afyer.afybroker.client.BrokerClient;
import net.afyer.afybroker.core.BrokerClientType;
import net.afyer.afybroker.core.BrokerGlobalConfig;
import net.afyer.afybroker.core.util.BoltUtils;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Objects;
import java.util.UUID;

/**
 * @author Nipuru
 * @since 2022/7/28 7:26
 */
@Getter
public class AfyBroker extends JavaPlugin {

    private BrokerClient brokerClient;

    @Override
    public void onLoad() {
        saveDefaultConfig();
        brokerClient = BrokerClient.newBuilder()
                .host(getConfig().getString("broker.host", BrokerGlobalConfig.BROKER_HOST))
                .port(getConfig().getInt("broker.port", BrokerGlobalConfig.BROKER_PORT))
                .name(getConfig().getString("broker.name", "bukkit-%unique_id%")
                        .replace("%unique_id%", UUID.randomUUID().toString().substring(0, 8))
                        .replace("%hostname%", Objects.toString(System.getenv("HOSTNAME"))))
                .addTags(getConfig().getStringList("broker.tags"))
                .type(BrokerClientType.BUKKIT)
                .registerUserProcessor(new SendPlayerChatBukkitProcessor())
                .registerUserProcessor(new BroadcastChatBukkitProcessor())
                .registerUserProcessor(new SendPlayerTitleBukkitProcessor())
                .registerUserProcessor(new SudoBukkitProcessor(this))
                .registerUserProcessor(new RequestPlayerInfoBukkitProcessor())
                .build();
        Broker.setClient(brokerClient);
    }

    @Override
    public void onEnable() {
        BoltUtils.initProtocols();
        ClassLoader oldLoader = Thread.currentThread().getContextClassLoader();
        try {
            Thread.currentThread().setContextClassLoader(getClassLoader());
            brokerClient.startup();
            brokerClient.ping();
        } catch (LifeCycleException e) {
            getLogger().severe("Broker client startup failed!");
            e.printStackTrace();
            Bukkit.shutdown();
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
        brokerClient.shutdown();
        BoltUtils.clearProtocols();
    }

    private void registerListeners() {
        new PlayerListener(this).register(this);
    }

}
