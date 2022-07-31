package net.afyer.broker.bukkit;

import org.bukkit.plugin.java.JavaPlugin;

/**
 * @author Nipuru
 * @since 2022/7/28 7:26
 */
public class AfyBroker extends JavaPlugin {

    @Override
    public void onEnable() {
        instance = this;
    }

    @Override
    public void onDisable() {

    }

    private static AfyBroker instance;

    public static AfyBroker getInstance() {
        return instance;
    }
}
