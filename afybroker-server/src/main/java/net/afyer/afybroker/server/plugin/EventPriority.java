package net.afyer.afybroker.server.plugin;

import lombok.experimental.UtilityClass;

/**
 * @author Nipuru
 * @since 2022/7/31 11:45
 */
@UtilityClass
public class EventPriority {
    public final byte LOWEST = -64;
    public final byte LOW = -32;
    public final byte NORMAL = 0;
    public final byte HIGH = 32;
    public final byte HIGHEST = 64;
}
