package net.afyer.afybroker.server.scheduler;

import net.afyer.afybroker.server.plugin.Plugin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @author Nipuru
 * @since 2022/7/31 12:25
 */
public class BrokerTask implements Runnable, ScheduledTask {

    private static final Logger LOGGER = LoggerFactory.getLogger(BrokerTask.class);

    private final BrokerScheduler sched;
    private final int id;
    private final Plugin owner;
    private final Runnable task;
    private final long delay;
    private final long period;
    private final AtomicBoolean running = new AtomicBoolean(true);

    public BrokerTask(BrokerScheduler sched, int id, Plugin owner, Runnable task,
                      long delay, long period, TimeUnit unit) {
        this.sched = sched;
        this.id = id;
        this.owner = owner;
        this.task = task;
        this.delay = unit.toMillis(delay);
        this.period = unit.toMillis(period);
    }

    public BrokerScheduler getSched() {
        return sched;
    }

    public int getId() {
        return id;
    }

    public Plugin getOwner() {
        return owner;
    }

    public Runnable getTask() {
        return task;
    }

    public long getDelay() {
        return delay;
    }

    public long getPeriod() {
        return period;
    }

    public AtomicBoolean getRunning() {
        return running;
    }

    @Override
    public void cancel() {
        boolean wasRunning = running.getAndSet(false);

        if (wasRunning) {
            sched.cancel0(this);
        }
    }

    @Override
    public void run() {
        if (delay > 0) {
            try {
                Thread.sleep(delay);
            } catch (InterruptedException ex) {
                Thread.currentThread().interrupt();
            }
        }

        while (running.get()) {
            try {
                task.run();
            } catch (Throwable t) {
                LOGGER.error(String.format("Task %s encountered an exception", this), t);
            }

            if (period <= 0) {
                break;
            }

            try {
                Thread.sleep(period);
            } catch (InterruptedException ex) {
                Thread.currentThread().interrupt();
            }
        }

        cancel();
    }
}
