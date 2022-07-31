package net.afyer.afybroker.server.plugin;

import com.google.common.base.Preconditions;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import net.afyer.afybroker.server.BrokerServer;

import java.io.File;
import java.io.InputStream;

/**
 * @author Nipuru
 * @since 2022/7/31 10:42
 */
@Getter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Plugin {

    PluginDescription description;
    BrokerServer server;
    File file;

    public Plugin() {
        ClassLoader classLoader = getClass().getClassLoader();
        Preconditions.checkState( classLoader instanceof PluginClassloader, "Plugin requires " + PluginClassloader.class.getName() );

        ((PluginClassloader)classLoader).init(this);
    }

    public void onLoad() {
    }


    public void onEnable() {
    }


    public void onDisable() {
    }

    public final File getDataFolder()
    {
        return new File( getServer().getPluginsFolder(), getDescription().getName() );
    }

    public final InputStream getResourceAsStream(String name) {
        return getClass().getClassLoader().getResourceAsStream( name );
    }

    final void init(BrokerServer server, PluginDescription description)
    {
        this.server = server;
        this.description = description;
        this.file = description.getFile();
    }
}
