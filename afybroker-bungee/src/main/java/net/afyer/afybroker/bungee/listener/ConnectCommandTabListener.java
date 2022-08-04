package net.afyer.afybroker.bungee.listener;

import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.event.TabCompleteEvent;
import net.md_5.bungee.event.EventHandler;

/**
 * @author Nipuru
 * @since 2022/8/3 19:18
 */
public class ConnectCommandTabListener extends AbstractListener {

    @EventHandler
    public void onTab(TabCompleteEvent event) {
        if (event.getCursor().startsWith("/connect ")) {
            ProxyServer.getInstance().getServers().values().stream().map(ServerInfo::getName).forEach(event.getSuggestions()::add);
        }
    }
}
