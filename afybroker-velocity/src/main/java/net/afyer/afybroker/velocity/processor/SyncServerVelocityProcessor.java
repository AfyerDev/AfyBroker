package net.afyer.afybroker.velocity.processor;

import com.alipay.remoting.AsyncContext;
import com.alipay.remoting.BizContext;
import com.alipay.remoting.rpc.protocol.AsyncUserProcessor;
import com.velocitypowered.api.proxy.server.RegisteredServer;
import com.velocitypowered.api.proxy.server.ServerInfo;
import net.afyer.afybroker.core.message.SyncServerMessage;
import net.afyer.afybroker.velocity.AfyBroker;

import java.net.InetSocketAddress;
import java.util.Optional;

public class SyncServerVelocityProcessor extends AsyncUserProcessor<SyncServerMessage> {

    private final AfyBroker plugin;

    public SyncServerVelocityProcessor(AfyBroker plugin) {
        this.plugin = plugin;
    }

    @Override
    public void handleRequest(BizContext bizCtx, AsyncContext asyncCtx, SyncServerMessage request) throws Exception {
        if (!plugin.isSyncEnable()) {
            return;
        }
        request.getServers().forEach((name, address) -> {
            Optional<RegisteredServer> server = plugin.getServer().getServer(name);
            String host = address.split(":")[0];
            int port = Integer.parseInt(address.split(":")[1]);
            InetSocketAddress socketAddress = InetSocketAddress.createUnresolved(host, port);
            if (server.isPresent()) {
                ServerInfo serverInfo = server.get().getServerInfo();
                if (serverInfo.getAddress().equals(socketAddress)) {
                    return;
                } else {
                    plugin.getServer().unregisterServer(serverInfo);
                }
            }
            plugin.getServer().registerServer(new ServerInfo(name, socketAddress));
        });
    }

    @Override
    public String interest() {
        return SyncServerMessage.class.getName();
    }
}
