package net.afyer.afybroker.server.plugin;

import com.google.common.base.Preconditions;
import com.google.common.io.ByteStreams;
import net.afyer.afybroker.server.BrokerServer;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLClassLoader;
import java.security.CodeSigner;
import java.security.CodeSource;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.jar.Manifest;

/**
 * @author Nipuru
 * @since 2022/7/31 11:02
 */
public class PluginClassloader extends URLClassLoader {

    protected static final Set<PluginClassloader> allLoaders = new CopyOnWriteArraySet<>();

    private final BrokerServer server;
    private final PluginDescription desc;
    private final JarFile jar;
    private final Manifest manifest;
    private final URL url;

    private Plugin plugin;

    static {
        ClassLoader.registerAsParallelCapable();
    }

    public PluginClassloader(BrokerServer server, PluginDescription desc, File file) throws IOException {
        super(new URL[]{file.toURI().toURL()});
        this.server = server;
        this.desc = desc;
        this.jar = new JarFile(file);
        this.manifest = jar.getManifest();
        this.url = file.toURI().toURL();

        allLoaders.add(this);
    }

    @Override
    protected Class<?> loadClass(String name, boolean resolve) throws ClassNotFoundException {
        return loadClass0(name, resolve, true);
    }

    protected Class<?> loadClass0(String name, boolean resolve, boolean checkOther) throws ClassNotFoundException {
        try {
            Class<?> result = super.loadClass(name, resolve);
            if (checkOther || result.getClassLoader() == this) {
                return result;
            }
        } catch (ClassNotFoundException ignored) {
        }

        if (checkOther) {
            for (PluginClassloader loader : allLoaders) {
                if (loader != this) {
                    try {
                        return loader.loadClass0(name, resolve, false);
                    } catch (ClassNotFoundException ignored) {
                    }
                }
            }
        }

        throw new ClassNotFoundException(name);
    }

    @Override
    protected Class<?> findClass(String name) throws ClassNotFoundException {
        String path = name.replace('.', '/').concat(".class");
        JarEntry entry = jar.getJarEntry(path);

        if (entry != null) {
            byte[] classBytes;

            try (InputStream is = jar.getInputStream(entry)) {
                classBytes = ByteStreams.toByteArray(is);
            } catch (IOException ex) {
                throw new ClassNotFoundException(name, ex);
            }

            int dot = name.lastIndexOf('.');
            if (dot != -1) {
                String pkgName = name.substring(0, dot);
                if (getPackage(pkgName) == null) {
                    try {
                        if (manifest != null) {
                            definePackage(pkgName, manifest, url);
                        } else {
                            definePackage(pkgName, null, null, null, null, null, null, null);
                        }
                    } catch (IllegalArgumentException ex) {
                        if (getPackage(pkgName) == null) {
                            throw new IllegalStateException("Cannot find package " + pkgName);
                        }
                    }
                }
            }

            CodeSigner[] signers = entry.getCodeSigners();
            CodeSource source = new CodeSource(url, signers);

            return defineClass(name, classBytes, 0, classBytes.length, source);
        }

        return super.findClass(name);
    }

    @Override
    public void close() throws IOException {
        try {
            super.close();
        } finally {
            jar.close();
        }
    }

    void init(Plugin plugin) {
        Preconditions.checkArgument(plugin != null, "plugin");
        Preconditions.checkArgument(plugin.getClass().getClassLoader() == this, "Plugin has incorrect ClassLoader");
        if (this.plugin != null) {
            throw new IllegalArgumentException("Plugin already initialized!");
        }

        this.plugin = plugin;
        plugin.init(server, desc);
    }
}
