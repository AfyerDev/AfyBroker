package net.afyer.afybroker.velocity.processor;

import com.alipay.remoting.AsyncContext;
import com.alipay.remoting.BizContext;
import com.alipay.remoting.rpc.protocol.AsyncUserProcessor;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ProxyServer;
import com.velocitypowered.api.proxy.server.RegisteredServer;
import net.afyer.afybroker.core.message.ConnectToServerMessage;
import net.afyer.afybroker.velocity.AfyBroker;

/**
 * @author Nipuru
 * @since 2022/9/6 17:35
 */
public class ConnectToServerVelocityProcessor extends AsyncUserProcessor<ConnectToServerMessage> {

    private final AfyBroker plugin;

    public ConnectToServerVelocityProcessor(AfyBroker plugin) {
        this.plugin = plugin;
    }

    @Override
    public void handleRequest(BizContext bizCtx, AsyncContext asyncCtx, ConnectToServerMessage message) {
        ProxyServer server = plugin.getServer();

        RegisteredServer target = server.getServer(message.getServerName()).orElse(null);
        if (target == null) return;

        Player player = server.getPlayer(message.getUniqueId()).orElse(null);
        if (player == null) return;

        player.createConnectionRequest(target).connect();
    }

    @Override
    public String interest() {
        return ConnectToServerMessage.class.getName();
    }
}
