package net.afyer.afybroker.server.plugin;

import com.google.common.base.Preconditions;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author Nipuru
 * @since 2022/11/21 16:38
 */
public class AsyncEvent<T> extends Event {

    private final Callback<T> done;
    private final Map<Plugin, AtomicInteger> intents = new ConcurrentHashMap<>();
    private final AtomicBoolean fired = new AtomicBoolean();
    private final AtomicInteger latch = new AtomicInteger();

    public AsyncEvent(Callback<T> done) {
        this.done = done;
    }

    public Callback<T> getDone() {
        return done;
    }

    public Map<Plugin, AtomicInteger> getIntents() {
        return intents;
    }

    public AtomicBoolean getFired() {
        return fired;
    }

    public AtomicInteger getLatch() {
        return latch;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        AsyncEvent<?> asyncEvent = (AsyncEvent<?>) o;
        return java.util.Objects.equals(done, asyncEvent.done) &&
                java.util.Objects.equals(intents, asyncEvent.intents) &&
                java.util.Objects.equals(fired, asyncEvent.fired) &&
                java.util.Objects.equals(latch, asyncEvent.latch);
    }

    @Override
    public int hashCode() {
        return java.util.Objects.hash(super.hashCode(), done, intents, fired, latch);
    }

    @Override
    public String toString() {
        return "AsyncEvent{" +
                "done=" + done +
                ", intents=" + intents +
                ", fired=" + fired +
                ", latch=" + latch +
                '}';
    }

    @Override
    @SuppressWarnings("unchecked")
    public void postCall() {
        if (latch.get() == 0) {
            done.done((T) this, null);
        }
        fired.set(true);
    }

    public void registerIntent(Plugin plugin) {
        Preconditions.checkState(!fired.get(), "Event %s has already been fired", this);

        AtomicInteger intentCount = intents.get(plugin);
        if (intentCount == null) {
            intents.put(plugin, new AtomicInteger(1));
        } else {
            intentCount.incrementAndGet();
        }
        latch.incrementAndGet();
    }

    @SuppressWarnings("unchecked")
    public void completeIntent(Plugin plugin) {
        AtomicInteger intentCount = intents.get(plugin);
        Preconditions.checkState(intentCount != null && intentCount.get() > 0, "Plugin %s has not registered intents for event %s", plugin, this);

        intentCount.decrementAndGet();
        if (fired.get()) {
            if (latch.decrementAndGet() == 0) {
                done.done((T) this, null);
            }
        } else {
            latch.decrementAndGet();
        }
    }
}