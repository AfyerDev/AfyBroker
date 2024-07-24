package net.afyer.afybroker.bungee.processor;

import com.alipay.remoting.BizContext;
import com.alipay.remoting.rpc.protocol.SyncUserProcessor;
import net.afyer.afybroker.core.message.RequestPlayerInfoMessage;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class RequestPlayerInfoBungeeProcessor extends SyncUserProcessor<RequestPlayerInfoMessage> {
    @Override
    public Object handleRequest(BizContext bizCtx, RequestPlayerInfoMessage request) {
        Map<UUID, String> map = new HashMap<>();
        for (ProxiedPlayer player : ProxyServer.getInstance().getPlayers()) {
            map.put(player.getUniqueId(), player.getName());
        }
        return map;
    }

    @Override
    public String interest() {
        return RequestPlayerInfoMessage.class.getName();
    }
}
