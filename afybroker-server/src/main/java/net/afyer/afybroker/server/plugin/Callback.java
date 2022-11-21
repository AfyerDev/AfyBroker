package net.afyer.afybroker.server.plugin;

/**
 * @author Nipuru
 * @since 2022/11/21 16:39
 */
public interface Callback<V> {
    void done(V result, Throwable error);
}
