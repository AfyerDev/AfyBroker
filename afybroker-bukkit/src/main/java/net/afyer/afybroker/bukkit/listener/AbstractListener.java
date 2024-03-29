package net.afyer.afybroker.bukkit.listener;

import org.bukkit.Bukkit;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;

/**
 * @author Nipuru
 * @since 2023/09/29 12:26
 */
public class AbstractListener implements Listener {

    public void register(Plugin plugin) {
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }
}
