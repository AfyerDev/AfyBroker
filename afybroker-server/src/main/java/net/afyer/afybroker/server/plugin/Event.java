package net.afyer.afybroker.server.plugin;

import net.afyer.afybroker.server.Broker;

/**
 * @author Nipuru
 * @since 2022/7/31 11:36
 */
public class Event {
    public void postCall() {}

    public boolean call() {
        Broker.getPluginManager().callEvent(this);
        if (this instanceof Cancellable) {
            return !((Cancellable) this).isCancelled();
        }
        return true;
    }
}
