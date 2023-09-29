package net.afyer.afybroker.bungee.listener;

import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.api.plugin.Plugin;

/**
 * @author Nipuru
 * @since 2023/09/29 12:27
 */
public class AbstractListener implements Listener {

    public void register(Plugin plugin) {
        ProxyServer.getInstance().getPluginManager().registerListener(plugin, this);
    }
}
