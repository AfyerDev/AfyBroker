package net.afyer.afybroker.velocity.processor;

import com.alipay.remoting.BizContext;
import com.alipay.remoting.rpc.protocol.SyncUserProcessor;
import net.afyer.afybroker.core.message.PlayerHeartbeatValidateMessage;
import net.afyer.afybroker.velocity.AfyBroker;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * @author Nipuru
 * @since 2023/11/25 12:32
 */
public class PlayerHeartbeatValidateVelocityProcessor extends SyncUserProcessor<PlayerHeartbeatValidateMessage> {

    private final AfyBroker plugin;

    public PlayerHeartbeatValidateVelocityProcessor(AfyBroker plugin) {
        this.plugin = plugin;
    }

    @Override
    public Object handleRequest(BizContext bizCtx, PlayerHeartbeatValidateMessage request) {
        // 包含验证失败（已离线）的玩家
        List<UUID> response = new ArrayList<>();
        for (UUID uniqueId : request.getUniqueIdList()) {
            if (!plugin.getServer().getPlayer(uniqueId).isPresent()) {
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
