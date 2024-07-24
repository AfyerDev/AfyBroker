package net.afyer.afybroker.bukkit.processor;

import com.alipay.remoting.AsyncContext;
import com.alipay.remoting.BizContext;
import com.alipay.remoting.rpc.protocol.AsyncUserProcessor;
import net.afyer.afybroker.core.message.SendPlayerMessageMessage;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

/**
 * @author Nipuru
 * @since 2022/8/5 10:08
 */
public class SendPlayerChatBukkitProcessor extends AsyncUserProcessor<SendPlayerMessageMessage> {

    @Override
    public void handleRequest(BizContext bizCtx, AsyncContext asyncCtx, SendPlayerMessageMessage request) {
        Player target = Bukkit.getPlayer(request.getUniqueId());

        if (target == null) return;

        target.sendMessage(request.getMessage());
    }

    @Override
    public String interest() {
        return SendPlayerMessageMessage.class.getName();
    }
}
