package net.afyer.afybroker.server.scheduler;

import net.afyer.afybroker.server.plugin.Plugin;

import java.util.concurrent.TimeUnit;

/**
 * @author Nipuru
 * @since 2022/7/31 12:22
 */
public interface TaskScheduler {

    void cancel(int id);

    void cancel(ScheduledTask task);

    int cancel(Plugin plugin);

    ScheduledTask runAsync(Plugin owner, Runnable task);

    ScheduledTask schedule(Plugin owner, Runnable task, long delay, TimeUnit unit);

    ScheduledTask schedule(Plugin owner, Runnable task, long delay, long period, TimeUnit unit);

}
