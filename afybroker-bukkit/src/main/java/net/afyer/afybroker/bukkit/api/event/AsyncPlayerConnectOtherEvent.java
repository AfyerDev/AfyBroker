package net.afyer.afybroker.bukkit.api.event;

import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;
import org.jetbrains.annotations.NotNull;

/**
 * @author Nipuru
 * @since 2022/8/3 18:13
 */
public class AsyncPlayerConnectOtherEvent extends PlayerEvent implements Cancellable {

    private static final HandlerList HANDLERS = new HandlerList();
    private final String server;
    private boolean cancel;

    public AsyncPlayerConnectOtherEvent(@NotNull Player who, String server) {
        super(who, true);
        this.server = server;
    }

    public String getServer() {
        return server;
    }

    @NotNull
    @Override
    public HandlerList getHandlers() {
        return HANDLERS;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }

    @Override
    public boolean isCancelled() {
        return cancel;
    }

    @Override
    public void setCancelled(boolean cancel) {
        this.cancel = cancel;
    }
}
