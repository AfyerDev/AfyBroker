package net.afyer.afybroker.bungee.processor;

import com.alipay.remoting.AsyncContext;
import com.alipay.remoting.BizContext;
import com.alipay.remoting.rpc.protocol.AsyncUserProcessor;
import lombok.Setter;
import net.afyer.afybroker.client.BrokerClient;
import net.afyer.afybroker.client.aware.BrokerClientAware;
import net.afyer.afybroker.core.message.SendPlayerChatMessage;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;

/**
 * @author Nipuru
 * @since 2022/8/5 10:08
 */
public class SendPlayerChatBungeeProcessor extends AsyncUserProcessor<SendPlayerChatMessage> implements BrokerClientAware {

    @Setter
    BrokerClient brokerClient;

    @Override
    public void handleRequest(BizContext bizCtx, AsyncContext asyncCtx, SendPlayerChatMessage request) {
        ProxiedPlayer player = ProxyServer.getInstance().getPlayer(request.getUid());

        if (player == null) {
            return;
        }

        player.sendMessage(ChatMessageType.CHAT, TextComponent.fromLegacyText(request.getMessage()));
    }

    @Override
    public String interest() {
        return SendPlayerChatMessage.class.getName();
    }
}
