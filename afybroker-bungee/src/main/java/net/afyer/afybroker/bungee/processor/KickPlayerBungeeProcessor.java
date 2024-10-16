package net.afyer.afybroker.bungee.processor;

import com.alipay.remoting.AsyncContext;
import com.alipay.remoting.BizContext;
import com.alipay.remoting.rpc.protocol.AsyncUserProcessor;
import net.afyer.afybroker.core.message.KickPlayerMessage;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;

/**
 * @author Nipuru
 * @since 2022/10/10 10:34
 */
public class KickPlayerBungeeProcessor extends AsyncUserProcessor<KickPlayerMessage> {

    @Override
    public void handleRequest(BizContext bizCtx, AsyncContext asyncCtx, KickPlayerMessage request) {
        ProxiedPlayer player = ProxyServer.getInstance().getPlayer(request.getUniqueId());

        if (player == null) {
            return;
        }

        if (request.getMessage() != null) {
            BaseComponent[] baseComponents = TextComponent.fromLegacyText(request.getMessage());
            player.disconnect(baseComponents);
        } else {
            player.disconnect();
        }

    }

    @Override
    public String interest() {
        return KickPlayerMessage.class.getName();
    }
}
