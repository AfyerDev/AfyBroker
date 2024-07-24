package net.afyer.afybroker.bungee.processor.connection;

import com.alipay.remoting.Connection;
import com.alipay.remoting.ConnectionEventProcessor;
import net.afyer.afybroker.bungee.AfyBroker;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.TextComponent;

/**
 * @author Nipuru
 * @since 2023/08/11 08:58
 */
public class CloseEventBungeeProcessor implements ConnectionEventProcessor {

    private final AfyBroker plugin;

    public CloseEventBungeeProcessor(AfyBroker plugin) {
        this.plugin = plugin;
    }

    @Override
    public void onEvent(String remoteAddress, Connection connection) {
        ProxyServer server = ProxyServer.getInstance();

        boolean kickOnClose = plugin.getConfig().getBoolean("player.kick-on-close", false);
        if (kickOnClose) {
            TextComponent msg = new TextComponent("服务器网关已关闭");
            msg.setColor(ChatColor.RED);
            server.getPlayers().forEach(player -> player.disconnect(msg));
        }
    }
}
