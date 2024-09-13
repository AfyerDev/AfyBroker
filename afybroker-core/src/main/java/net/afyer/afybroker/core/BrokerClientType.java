package net.afyer.afybroker.core;

/**
 * 客户端类型
 *
 * @author Nipuru
 * @since 2022/7/30 16:15
 */
public interface BrokerClientType {

    /** BungeeCord 客户端 */
    String PROXY = "proxy";

    /** Bukkit 客户端 */
    String SERVER = "server";

    /** 未知类型的客户端 */
    String UNKNOWN = "unknown";

}
