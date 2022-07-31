package net.afyer.broker.bukkit;

import net.afyer.afybroker.core.FileConfig;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.io.IOException;

/**
 * @author Nipuru
 * @since 2022/7/28 17:57
 */
public class BukkitFileConfig extends FileConfig<ConfigurationSection> {

    private final Plugin plugin;
    private FileConfiguration configuration;

    public BukkitFileConfig(String path, Plugin plugin){
        super(new File(path));
        this.plugin = plugin;
        init();
        save();
    }

    private void init() {
        File configFile = getFile();
        if (!configFile.exists()) {
            plugin.saveResource(configFile.getPath(), true);
        }
    }

    @Override
    public void save() {
        File configFile = getFile();
        try {
            configuration.save(configFile);
        } catch (IOException e) {
            throw new IllegalStateException(e.getMessage(), e.getCause());
        }
    }

    @Override
    public void reload() {
        File configFile = getFile();
        configuration = YamlConfiguration.loadConfiguration(configFile);
    }

    @Override
    public ConfigurationSection get() {
        return configuration;
    }
}
