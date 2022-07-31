package net.afyer.afybroker.server.plugin;

/**
 * @author Nipuru
 * @since 2022/7/31 10:45
 */
public interface TabExecutor {
    Iterable<String> onTabComplete(String[] args);
}
