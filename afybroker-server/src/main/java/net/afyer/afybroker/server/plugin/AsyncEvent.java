package net.afyer.afybroker.server.plugin;

import com.google.common.base.Preconditions;
import lombok.*;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author Nipuru
 * @since 2022/11/21 16:38
 */
@Data
@Getter(AccessLevel.NONE)
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class AsyncEvent<T> extends Event {

    private final Callback<T> done;
    private final Map<Plugin, AtomicInteger> intents = new ConcurrentHashMap<>();
    private final AtomicBoolean fired = new AtomicBoolean();
    private final AtomicInteger latch = new AtomicInteger();

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