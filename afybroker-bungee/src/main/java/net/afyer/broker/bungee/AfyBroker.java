package net.afyer.broker.bungee;

import net.afyer.broker.bungee.listener.PlayerListener;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.config.YamlConfiguration;

/**
 * @author Nipuru
 * @since 2022/7/28 7:26
 */
public class AfyBroker extends Plugin {


    @Override
    public void onEnable() {
        BungeeFileConfig config = new BungeeFileConfig("config.yml", this, YamlConfiguration.class);

        new PlayerListener().register(this);
    }

    @Override
    public void onDisable() {

    }

}
