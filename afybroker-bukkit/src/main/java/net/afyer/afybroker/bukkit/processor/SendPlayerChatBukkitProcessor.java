package net.afyer.afybroker.bukkit.processor;

import com.alipay.remoting.AsyncContext;
import com.alipay.remoting.BizContext;
import net.afyer.afybroker.client.processor.BrokerAsyncUserProcessor;
import net.afyer.afybroker.core.message.SendPlayerChatMessage;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

/**
 * @author Nipuru
 * @since 2022/8/5 10:08
 */
public class SendPlayerChatBukkitProcessor extends BrokerAsyncUserProcessor<SendPlayerChatMessage> {

    @Override
    public void handleRequest(BizContext bizCtx, AsyncContext asyncCtx, SendPlayerChatMessage request) {
        Player target = Bukkit.getPlayer(request.getUid());

        if (target == null) {
            return;
        }

        target.sendMessage(request.getMessage());
    }

    @Override
    public String interest() {
        return SendPlayerChatMessage.class.getName();
    }
}
