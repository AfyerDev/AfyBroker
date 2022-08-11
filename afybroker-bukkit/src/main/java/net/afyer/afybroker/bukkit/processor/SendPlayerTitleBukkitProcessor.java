package net.afyer.afybroker.bukkit.processor;

import com.alipay.remoting.AsyncContext;
import com.alipay.remoting.BizContext;
import com.alipay.remoting.rpc.protocol.AsyncUserProcessor;
import net.afyer.afybroker.core.message.SendPlayerTitleMessage;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

/**
 * @author Nipuru
 * @since 2022/8/11 9:04
 */
public class SendPlayerTitleBukkitProcessor extends AsyncUserProcessor<SendPlayerTitleMessage> {

    @Override
    public void handleRequest(BizContext bizCtx, AsyncContext asyncCtx, SendPlayerTitleMessage request) {
        Player player = Bukkit.getPlayer(request.getPlayer());

        if (player == null) {
            return;
        }

        player.sendTitle(request.getTitle(), request.getSubtitle(), request.getFadein(), request.getStay(), request.getFadeout());
    }

    @Override
    public String interest() {
        return SendPlayerTitleMessage.class.getName();
    }
}
