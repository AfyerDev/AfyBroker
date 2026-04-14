package net.afyer.afybroker.core.observability;

public class PlayerObservation {
    private final PlayerEventType eventType;
    private final int onlinePlayers;

    public PlayerObservation(PlayerEventType eventType, int onlinePlayers) {
        this.eventType = eventType;
        this.onlinePlayers = onlinePlayers;
    }

    public PlayerEventType getEventType() {
        return eventType;
    }

    public int getOnlinePlayers() {
        return onlinePlayers;
    }
}
