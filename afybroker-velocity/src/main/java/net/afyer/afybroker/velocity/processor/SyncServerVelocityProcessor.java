package net.afyer.afybroker.velocity.processor;

import com.alipay.remoting.AsyncContext;
import com.alipay.remoting.BizContext;
import com.alipay.remoting.rpc.protocol.AsyncUserProcessor;
import com.velocitypowered.api.proxy.server.RegisteredServer;
import com.velocitypowered.api.proxy.server.ServerInfo;
import lombok.AllArgsConstructor;
import net.afyer.afybroker.core.message.SyncServerMessage;
import net.afyer.afybroker.velocity.AfyBroker;

import java.net.InetSocketAddress;
import java.util.Optional;

@AllArgsConstructor
public class SyncServerVelocityProcessor extends AsyncUserProcessor<SyncServerMessage> {

    private final AfyBroker plugin;

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
            if (server.isPresent() && server.get().getServerInfo().getAddress().equals(socketAddress)) {
                return;
            }
            ServerInfo serverInfo = new ServerInfo(name, socketAddress);
            plugin.getServer().registerServer(serverInfo);
        });
    }

    @Override
    public String interest() {
        return SyncServerMessage.class.getName();
    }
}
