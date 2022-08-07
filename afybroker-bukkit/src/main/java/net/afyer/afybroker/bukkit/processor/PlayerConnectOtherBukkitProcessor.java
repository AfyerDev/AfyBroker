package net.afyer.afybroker.bukkit.processor;

import com.alipay.remoting.AsyncContext;
import com.alipay.remoting.BizContext;
import com.alipay.remoting.rpc.protocol.AsyncUserProcessor;
import lombok.Setter;
import net.afyer.afybroker.bukkit.AfyBroker;
import net.afyer.afybroker.bukkit.BukkitKit;
import net.afyer.afybroker.client.BrokerClient;
import net.afyer.afybroker.client.aware.BrokerClientAware;
import net.afyer.afybroker.core.message.PlayerConnectOtherMessage;

/**
 * @author Nipuru
 * @since 2022/8/3 18:07
 */

public class PlayerConnectOtherBukkitProcessor extends AsyncUserProcessor<PlayerConnectOtherMessage> implements BrokerClientAware {

    @Setter
    BrokerClient brokerClient;

    private final AfyBroker plugin;

    public PlayerConnectOtherBukkitProcessor(AfyBroker plugin) {
        this.plugin = plugin;
    }

    @Override
    public void handleRequest(BizContext bizCtx, AsyncContext asyncCtx, PlayerConnectOtherMessage request) {

        BukkitKit.playerConnectOther(request.getUid(), request.getServer());

    }

    @Override
    public String interest() {
        return PlayerConnectOtherMessage.class.getName();
    }
}
