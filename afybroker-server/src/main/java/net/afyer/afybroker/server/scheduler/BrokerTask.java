package net.afyer.afybroker.server.scheduler;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import net.afyer.afybroker.server.plugin.Plugin;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @author Nipuru
 * @since 2022/7/31 12:25
 */
@Slf4j
@Getter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class BrokerTask implements Runnable, ScheduledTask {

    final BrokerScheduler sched;
    final int id;
    final Plugin owner;
    final Runnable task;
    final long delay;
    final long period;
    final AtomicBoolean running = new AtomicBoolean(true);

    public BrokerTask(BrokerScheduler sched, int id, Plugin owner, Runnable task,
                      long delay, long period, TimeUnit unit) {
        this.sched = sched;
        this.id = id;
        this.owner = owner;
        this.task = task;
        this.delay = unit.toMillis(delay);
        this.period = unit.toMillis(period);
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
                log.error(String.format("Task %s encountered an exception", this), t);
            }

            // If we have a period of 0 or less, only run once
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
