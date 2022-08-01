package net.afyer.afybroker.server.config;

import net.afyer.afybroker.core.FileConfig;
import net.afyer.afybroker.server.plugin.Plugin;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

/**
 * @author Nipuru
 * @since 2022/7/31 12:16
 */
public class BrokerFileConfig extends FileConfig<Configuration> {

    private final Plugin plugin;
    private Configuration configuration;
    private final Class<? extends ConfigurationProvider> provider;

    public BrokerFileConfig(String path, Plugin plugin, Class<? extends ConfigurationProvider> provider) {
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
