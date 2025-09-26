package net.afyer.afybroker.server.plugin;


/**
 * @author Nipuru
 * @since 2022/7/31 11:45
 */
public interface EventPriority {
    byte LOWEST = -64;
    byte LOW = -32;
    byte NORMAL = 0;
    byte HIGH = 32;
    byte HIGHEST = 64;
}
