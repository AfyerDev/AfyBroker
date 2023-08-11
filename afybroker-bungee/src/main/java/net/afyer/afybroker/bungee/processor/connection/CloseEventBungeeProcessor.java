package net.afyer.afybroker.bungee.processor.connection;

import com.alipay.remoting.Connection;
import com.alipay.remoting.ConnectionEventProcessor;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.TextComponent;

/**
 * @author Nipuru
 * @since 2023/08/11 08:58
 */
public class CloseEventBungeeProcessor implements ConnectionEventProcessor {
    @Override
    public void onEvent(String remoteAddress, Connection connection) {
        ProxyServer server = ProxyServer.getInstance();
        TextComponent msg = new TextComponent("服务器网关已关闭");
        msg.setColor(ChatColor.RED);
        server.getPlayers().forEach(player -> player.disconnect(msg));
    }
}
