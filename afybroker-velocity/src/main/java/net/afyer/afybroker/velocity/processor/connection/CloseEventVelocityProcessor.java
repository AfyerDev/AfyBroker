package net.afyer.afybroker.velocity.processor.connection;

import com.alipay.remoting.Connection;
import com.alipay.remoting.ConnectionEventProcessor;
import com.velocitypowered.api.proxy.ProxyServer;
import net.afyer.afybroker.velocity.AfyBroker;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;

/**
 * @author Nipuru
 * @since 2023/08/11 08:58
 */
public class CloseEventVelocityProcessor implements ConnectionEventProcessor {

    private final AfyBroker plugin;

    public CloseEventVelocityProcessor(AfyBroker plugin) {
        this.plugin = plugin;
    }

    @Override
    public void onEvent(String remoteAddress, Connection connection) {
        ProxyServer server = plugin.getServer();

        if (plugin.getConfig().getNode("player", "kick-on-close").getBoolean(false)) {

            TextComponent msg = Component
                    .text("服务器网关已关闭")
                    .color(NamedTextColor.RED);
            server.getAllPlayers().forEach(player -> player.disconnect(msg));
        }
    }
}
