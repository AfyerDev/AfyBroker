package net.afyer.afybroker.bukkit.processor;

import com.alipay.remoting.AsyncContext;
import com.alipay.remoting.BizContext;
import com.alipay.remoting.rpc.protocol.AsyncUserProcessor;
import net.afyer.afybroker.core.message.KickPlayerMessage;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

/**
 * @author Nipuru
 * @since 2022/10/10 10:34
 */
public class KickPlayerBukkitProcessor extends AsyncUserProcessor<KickPlayerMessage> {

    @Override
    public void handleRequest(BizContext bizCtx, AsyncContext asyncCtx, KickPlayerMessage request) {
        Player player = Bukkit.getPlayer(request.getPlayer());

        if (player == null) {
            return;
        }

        player.kick(Component.text(request.getMessage()));
    }

    @Override
    public String interest() {
        return KickPlayerMessage.class.getName();
    }
}
