package net.afyer.afybroker.bungee;

import net.afyer.afybroker.core.FileConfig;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

/**
 * @author Nipuru
 * @since 2022/7/28 17:45
 */
public class BungeeFileConfig extends FileConfig<Configuration> {

    private final Plugin plugin;
    private Configuration configuration;
    private final Class<? extends ConfigurationProvider> provider;

    public BungeeFileConfig(String path, Plugin plugin, Class<? extends ConfigurationProvider> provider) {
        super(new File(plugin.getDataFolder(), path), path);
        this.plugin = plugin;
        this.provider = provider;
        init();
        reload();
    }

    private void init() {
        File configFile = getFile();
        if (!configFile.exists()) {
            configFile.getParentFile().mkdirs();
            try {
                Files.copy(plugin.getResourceAsStream(getName()), configFile.toPath());
            } catch (IOException e) {
                throw new IllegalStateException(e.getMessage(), e.getCause());
            }
        }
    }

    @Override
    public void save() {
        try {
            ConfigurationProvider.getProvider(provider).save(configuration, getFile());
        } catch (IOException e) {
            throw new IllegalStateException(e.getMessage(), e.getCause());
        }
    }

    @Override
    public void reload() {
        try {
            this.configuration = ConfigurationProvider.getProvider(provider).load(getFile());
        } catch (IOException e) {
            throw new IllegalStateException(e.getMessage(), e.getCause());
        }
    }

    @Override
    public Configuration get() {
        return configuration;
    }
}
