package net.afyer.afybroker.server.plugin;

/**
 * @author Nipuru
 * @since 2022/8/13 18:17
 */
public class BrokerClassLoader extends ClassLoader {

    @Override
    protected Class<?> loadClass(String name, boolean resolve) throws ClassNotFoundException {
        try {
            return super.loadClass(name, resolve);
        } catch (ClassNotFoundException ignored) {
        }

        for (PluginClassloader loader : PluginClassloader.allLoaders) {
            try {
                return loader.loadClass0(name, resolve, false);
            } catch (ClassNotFoundException ignored) {
            }
        }

        throw new ClassNotFoundException(name);
    }
}
