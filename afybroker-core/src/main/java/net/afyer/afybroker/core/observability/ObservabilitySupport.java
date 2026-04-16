package net.afyer.afybroker.core.observability;

public final class ObservabilitySupport {

    private ObservabilitySupport() {
    }

    public static String labelValue(String value) {
        if (value == null || value.trim().isEmpty()) {
            return "unknown";
        }
        return value.trim();
    }
}
