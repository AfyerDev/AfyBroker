package net.afyer.afybroker.bungee.processor;

import com.alipay.remoting.AsyncContext;
import com.alipay.remoting.BizContext;
import com.alipay.remoting.rpc.protocol.AsyncUserProcessor;
import net.afyer.afybroker.core.message.ConnectToServerMessage;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;

/**
 * @author Nipuru
 * @since 2022/9/6 17:35
 */
public class ConnectToServerBungeeProcessor extends AsyncUserProcessor<ConnectToServerMessage> {

    @Override
    public void handleRequest(BizContext bizCtx, AsyncContext asyncCtx, ConnectToServerMessage message) {
        ProxyServer bungee = ProxyServer.getInstance();

        ProxiedPlayer player = bungee.getPlayer(message.getPlayer());
        if (player == null) return;

        ServerInfo server = bungee.getServerInfo(message.getServer());
        if (server == null) return;

        player.connect(server);
    }

    @Override
    public String interest() {
        return ConnectToServerMessage.class.getName();
    }
}
