package net.afyer.afybroker.server.scheduler;

import net.afyer.afybroker.server.plugin.Plugin;

/**
 * @author Nipuru
 * @since 2022/7/31 12:21
 */
public interface ScheduledTask {

    int getId();

    Plugin getOwner();

    Runnable getTask();

    void cancel();

}
