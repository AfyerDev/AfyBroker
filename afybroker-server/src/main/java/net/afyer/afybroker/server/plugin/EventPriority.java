package net.afyer.afybroker.server.plugin;

import lombok.experimental.UtilityClass;

/**
 * @author Nipuru
 * @since 2022/7/31 11:45
 */
@UtilityClass
public class EventPriority {
    final byte LOWEST = -64;
    final byte LOW = -32;
    final byte NORMAL = 0;
    final byte HIGH = 32;
    final byte HIGHEST = 64;
}
