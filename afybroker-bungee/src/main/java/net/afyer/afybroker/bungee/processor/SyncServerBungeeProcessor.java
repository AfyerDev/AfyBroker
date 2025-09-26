package net.afyer.afybroker.bungee.processor;

import com.alipay.remoting.AsyncContext;
import com.alipay.remoting.BizContext;
import com.alipay.remoting.rpc.protocol.AsyncUserProcessor;
import net.afyer.afybroker.bungee.AfyBroker;
import net.afyer.afybroker.core.message.SyncServerMessage;
import net.md_5.bungee.Util;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.config.ServerInfo;

import java.net.SocketAddress;

public class SyncServerBungeeProcessor extends AsyncUserProcessor<SyncServerMessage> {
    private final AfyBroker plugin;

    public SyncServerBungeeProcessor(AfyBroker plugin) {
        this.plugin = plugin;
    }
    @Override
    public void handleRequest(BizContext bizCtx, AsyncContext asyncCtx, SyncServerMessage request) throws Exception {
        if (!plugin.isSyncEnable()) {
            return;
        }
        ProxyServer proxyServer = ProxyServer.getInstance();
        request.getServers().forEach((name, address) -> {
            ServerInfo serverInfo = proxyServer.getServerInfo(name);
            SocketAddress socketAddress = Util.getAddr(address);
            if (serverInfo != null && serverInfo.getSocketAddress().equals(socketAddress)) {
                return;
            }
            serverInfo = proxyServer.constructServerInfo(name, socketAddress, "", false);
            proxyServer.getServers().put(name, serverInfo);
        });
    }

    @Override
    public String interest() {
        return SyncServerMessage.class.getName();
    }
}
