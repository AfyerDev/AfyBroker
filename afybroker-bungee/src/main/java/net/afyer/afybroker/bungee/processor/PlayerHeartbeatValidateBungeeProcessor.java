package net.afyer.afybroker.bungee.processor;

import com.alipay.remoting.BizContext;
import com.alipay.remoting.rpc.protocol.SyncUserProcessor;
import net.afyer.afybroker.core.message.PlayerHeartbeatValidateMessage;
import net.md_5.bungee.api.ProxyServer;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * @author Nipuru
 * @since 2023/11/25 12:32
 */
public class PlayerHeartbeatValidateBungeeProcessor extends SyncUserProcessor<PlayerHeartbeatValidateMessage> {

    @Override
    public Object handleRequest(BizContext bizCtx, PlayerHeartbeatValidateMessage request) {
        // 包含验证失败（已离线）的玩家
        List<UUID> response = new ArrayList<>();
        for (UUID uniqueId : request.getUniqueIdList()) {
            if (ProxyServer.getInstance().getPlayer(uniqueId) == null) {
                response.add(uniqueId);
            }
        }
        return response;
    }

    @Override
    public String interest() {
        return PlayerHeartbeatValidateMessage.class.getName();
    }
}
