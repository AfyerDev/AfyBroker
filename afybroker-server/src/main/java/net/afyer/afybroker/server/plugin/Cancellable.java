package net.afyer.afybroker.server.plugin;

/**
 * @author Nipuru
 * @since 2022/12/4 21:35
 */
public interface Cancellable {

    void setCancelled(boolean cancel);

    boolean isCancelled();

}
