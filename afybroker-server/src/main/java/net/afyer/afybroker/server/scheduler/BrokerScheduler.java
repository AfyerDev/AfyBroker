package net.afyer.afybroker.server.scheduler;

import com.google.common.base.Preconditions;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.google.common.collect.Multimaps;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import gnu.trove.TCollections;
import gnu.trove.map.TIntObjectMap;
import gnu.trove.map.hash.TIntObjectHashMap;
import net.afyer.afybroker.server.BrokerServer;
import net.afyer.afybroker.server.plugin.Plugin;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author Nipuru
 * @since 2022/7/31 12:23
 */
public class BrokerScheduler implements TaskScheduler {

    private final BrokerServer server;
    private final Object lock = new Object();
    private final AtomicInteger taskCounter = new AtomicInteger();
    private final TIntObjectMap<BrokerTask> tasks = TCollections.synchronizedMap(new TIntObjectHashMap<>());
    private final Multimap<Plugin, BrokerTask> tasksByPlugin = Multimaps.synchronizedMultimap(HashMultimap.create());
    private final ExecutorService scheduler = new ThreadPoolExecutor(0, Integer.MAX_VALUE,
            60L, TimeUnit.SECONDS,
            new SynchronousQueue<>(),
            new ThreadFactoryBuilder().setDaemon(true).setNameFormat("Broker-Scheduler-Thread-%d").build());

    public BrokerScheduler(BrokerServer server) {
        this.server = server;
    }

    @Override
    public void cancel(int id) {
        BrokerTask task = tasks.get(id);
        Preconditions.checkArgument(task != null, "No task with id %s", id);

        task.cancel();
    }

    void cancel0(BrokerTask task) {
        synchronized (lock) {
            tasks.remove(task.getId());
            tasksByPlugin.values().remove(task);
        }
    }

    @Override
    public void cancel(ScheduledTask task) {
        task.cancel();
    }

    @Override
    public int cancel(Plugin plugin) {
        Set<ScheduledTask> toRemove = new HashSet<>();
        synchronized (lock) {
            for (ScheduledTask task : tasksByPlugin.get(plugin)) {
                toRemove.add(task);
            }
        }
        for (ScheduledTask task : toRemove) {
            cancel(task);
        }
        return toRemove.size();
    }

    @Override
    public ScheduledTask runAsync(Plugin owner, Runnable task) {
        return schedule(owner, task, 0, TimeUnit.MILLISECONDS);
    }

    @Override
    public ScheduledTask schedule(Plugin owner, Runnable task, long delay, TimeUnit unit) {
        return schedule(owner, task, delay, 0, unit);
    }

    @Override
    public ScheduledTask schedule(Plugin owner, Runnable task, long delay, long period,
                                  TimeUnit unit) {
        Preconditions.checkNotNull(owner, "owner");
        Preconditions.checkNotNull(task, "task");
        BrokerTask prepared = new BrokerTask(this, taskCounter.getAndIncrement(), owner,
                task, delay, period, unit);

        synchronized (lock) {
            tasks.put(prepared.getId(), prepared);
            tasksByPlugin.put(owner, prepared);
        }

        scheduler.execute(prepared);
        return prepared;
    }
}
