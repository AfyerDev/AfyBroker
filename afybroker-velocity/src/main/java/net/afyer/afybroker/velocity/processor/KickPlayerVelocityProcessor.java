package net.afyer.afybroker.velocity.processor;

import com.alipay.remoting.AsyncContext;
import com.alipay.remoting.BizContext;
import com.alipay.remoting.rpc.protocol.AsyncUserProcessor;
import lombok.AllArgsConstructor;
import net.afyer.afybroker.core.message.KickPlayerMessage;
import net.afyer.afybroker.velocity.AfyBroker;
import net.kyori.adventure.text.Component;

/**
 * @author Nipuru
 * @since 2022/10/10 10:34
 */
@AllArgsConstructor
public class KickPlayerVelocityProcessor extends AsyncUserProcessor<KickPlayerMessage> {

    private final AfyBroker plugin;

    @Override
    public void handleRequest(BizContext bizCtx, AsyncContext asyncCtx, KickPlayerMessage request) {
        plugin.getServer().getPlayer(request.getUniqueId())
                .ifPresent(player -> player.disconnect(Component.text(request.getMessage())));
    }

    @Override
    public String interest() {
        return KickPlayerMessage.class.getName();
    }
}
