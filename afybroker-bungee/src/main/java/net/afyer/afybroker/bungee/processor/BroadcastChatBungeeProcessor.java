package net.afyer.afybroker.bungee.processor;

import com.alipay.remoting.AsyncContext;
import com.alipay.remoting.BizContext;
import com.alipay.remoting.rpc.protocol.AsyncUserProcessor;
import net.afyer.afybroker.core.message.BroadcastChatMessage;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;

/**
 * @author Nipuru
 * @since 2022/8/10 11:28
 */
public class BroadcastChatBungeeProcessor extends AsyncUserProcessor<BroadcastChatMessage> {

    @Override
    public void handleRequest(BizContext bizCtx, AsyncContext asyncCtx, BroadcastChatMessage request) {
        BaseComponent[] components = TextComponent.fromLegacyText(request.getMessage());
        ProxyServer.getInstance().getPlayers().forEach(player -> player.sendMessage(ChatMessageType.CHAT, components));
    }

    @Override
    public String interest() {
        return BroadcastChatMessage.class.getName();
    }
}
