package net.afyer.afybroker.velocity.processor;

import com.alipay.remoting.BizContext;
import com.alipay.remoting.rpc.protocol.SyncUserProcessor;
import com.velocitypowered.api.proxy.Player;
import lombok.AllArgsConstructor;
import net.afyer.afybroker.core.message.RequestPlayerInfoMessage;
import net.afyer.afybroker.velocity.AfyBroker;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@AllArgsConstructor
public class RequestPlayerInfoVelocityProcessor extends SyncUserProcessor<RequestPlayerInfoMessage> {

    private final AfyBroker plugin;

    @Override
    public Object handleRequest(BizContext bizCtx, RequestPlayerInfoMessage request) throws Exception {
        Map<UUID, String> map = new HashMap<>();
        for (Player player : plugin.getServer().getAllPlayers()) {
            map.put(player.getUniqueId(), player.getUsername());
        }
        return map;
    }

    @Override
    public String interest() {
        return RequestPlayerInfoMessage.class.getName();
    }
}
