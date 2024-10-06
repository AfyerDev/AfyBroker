package net.afyer.afybroker.bungee.processor;

import com.alipay.remoting.AsyncContext;
import com.alipay.remoting.BizContext;
import com.alipay.remoting.rpc.protocol.AsyncUserProcessor;
import net.afyer.afybroker.core.message.ConnectToServerMessage;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.lang.reflect.Field;
import java.util.Collection;
import java.util.Objects;

/**
 * @author Nipuru
 * @since 2022/9/6 17:35
 */
public class ConnectToServerBungeeProcessor extends AsyncUserProcessor<ConnectToServerMessage> {

    private static Field PENDING_CONNECT_FIELD;

    @SuppressWarnings({"unchecked", "SynchronizationOnLocalVariableOrMethodParameter"})
    @Override
    public void handleRequest(BizContext bizCtx, AsyncContext asyncCtx, ConnectToServerMessage message) {
        ProxyServer bungee = ProxyServer.getInstance();

        ServerInfo target = bungee.getServerInfo(message.getServerName());
        if (target == null) return;

        ProxiedPlayer player = bungee.getPlayer(message.getUniqueId());
        if (player == null) return;

        synchronized (player) {
            if (player.getServer() != null && Objects.equals(player.getServer().getInfo(), target)) return;

            try {
                if (PENDING_CONNECT_FIELD == null) {
                    Field field = player.getClass().getDeclaredField("pendingConnects");
                    field.setAccessible(true);
                    PENDING_CONNECT_FIELD = field;
                }
                if (((Collection<ServerInfo>)PENDING_CONNECT_FIELD.get(player)).contains(target)) {
                    return;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            player.connect(target);
        }
    }

    @Override
    public String interest() {
        return ConnectToServerMessage.class.getName();
    }
}
