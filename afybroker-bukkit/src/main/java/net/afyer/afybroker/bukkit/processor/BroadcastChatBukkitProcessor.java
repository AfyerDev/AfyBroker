package net.afyer.afybroker.bukkit.processor;

import com.alipay.remoting.AsyncContext;
import com.alipay.remoting.BizContext;
import com.alipay.remoting.rpc.protocol.AsyncUserProcessor;
import net.afyer.afybroker.core.message.BroadcastChatMessage;
import org.bukkit.Bukkit;

/**
 * @author Nipuru
 * @since 2022/8/10 11:29
 */
public class BroadcastChatBukkitProcessor extends AsyncUserProcessor<BroadcastChatMessage> {

    @Override
    public void handleRequest(BizContext bizCtx, AsyncContext asyncCtx, BroadcastChatMessage request) {
        Bukkit.getOnlinePlayers().forEach(player -> player.sendMessage(request.getMessage()));
    }

    @Override
    public String interest() {
        return BroadcastChatMessage.class.getName();
    }
}
