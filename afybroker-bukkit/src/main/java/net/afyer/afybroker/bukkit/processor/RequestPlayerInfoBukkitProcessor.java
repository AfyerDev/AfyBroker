package net.afyer.afybroker.bukkit.processor;

import com.alipay.remoting.BizContext;
import com.alipay.remoting.rpc.protocol.SyncUserProcessor;
import net.afyer.afybroker.core.message.RequestPlayerInfoMessage;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class RequestPlayerInfoBukkitProcessor extends SyncUserProcessor<RequestPlayerInfoMessage> {

    @Override
    public Object handleRequest(BizContext bizCtx, RequestPlayerInfoMessage request) {
        List<UUID> list = new ArrayList<>();
        for (Player player : Bukkit.getOnlinePlayers()) {
            list.add(player.getUniqueId());
        }
        return list;
    }

    @Override
    public String interest() {
        return RequestPlayerInfoMessage.class.getName();
    }
}
