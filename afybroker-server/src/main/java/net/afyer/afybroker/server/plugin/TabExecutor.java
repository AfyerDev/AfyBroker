package net.afyer.afybroker.server.plugin;

/**
 * @author Nipuru
 * @since 2022/8/6 8:29
 */
public interface TabExecutor {

    Iterable<String> onTabComplete(String[] args);

}
