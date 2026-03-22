package net.afyer.afybroker.server;

import com.alipay.remoting.BizContext;
import com.alipay.remoting.rpc.RpcServer;
import net.afyer.afybroker.server.plugin.PluginManager;
import net.afyer.afybroker.server.proxy.BrokerClientItem;
import net.afyer.afybroker.server.proxy.BrokerClientManager;
import net.afyer.afybroker.server.proxy.BrokerPlayer;
import net.afyer.afybroker.server.proxy.BrokerPlayerManager;
import net.afyer.afybroker.server.scheduler.BrokerScheduler;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.UUID;

/**
 * @author Nipuru
 * @since 2022/8/13 9:34
 */
public class Broker {

    /**
     * broker 服务端
     */
    private static BrokerServer server;

    public static BrokerServer getServer() {
        return server;
    }

    private Broker() {}

    /** 设置 broker 服务端 */
    public static void setServer(BrokerServer brokerServer) {
        if (Broker.server != null) {
            throw new UnsupportedOperationException("Cannot redefine singleton instance");
        }
        Broker.server = brokerServer;
    }

    /** 获取 rpc 服务端 */
    public static RpcServer getRpcServer() {
        return server.getRpcServer();
    }

    /**
     * 获取服务端端口
     */
    public static int getPort() {
        return server.getPort();
    }

    /**
     * 获取插件管理器
     */
    public static PluginManager getPluginManager() {
        return server.getPluginManager();
    }

    /**
     * 获取计划任务管理器
     */
    public static BrokerScheduler getScheduler() {
        return server.getScheduler();
    }

    /**
     * 获取客户端代理管理器
     */
    public static BrokerClientManager getClientManager() {
        return server.getClientManager();
    }

    /**
     * 获取玩家代理管理器
     */
    public static BrokerPlayerManager getPlayerManager() {
        return server.getPlayerManager();
    }

    /**
     * 通过uid获取玩家代理
     */
    @Nullable
    public static BrokerPlayer getPlayer(UUID uuid) {
        return server.getPlayer(uuid);
    }

    /**
     * 通过名称获取玩家代理
     */
    @Nullable
    public static BrokerPlayer getPlayer(String name) {
        return server.getPlayer(name);
    }

    /**
     * 通过名称获取客户端代理
     */
    @Nullable
    public static BrokerClientItem getClient(String name) {
        return server.getClient(name);
    }

    /**
     * 通过 {@link BizContext} 获取玩家代理
     */
    @Nullable
    public static BrokerClientItem getClient(BizContext bizContext) {
        return server.getClient(bizContext);
    }

    /**
     * 获取插件目录
     */
    public static File getPluginsFolder() {
        return server.getPluginsFolder();
    }

    // ==================== 属性操作 ====================

    /**
     * 设置服务器全局属性
     */
    public static void setServerAttribute(String key, byte[] value) {
        server.setAttribute(key, value);
    }

    /**
     * 获取服务器全局属性
     */
    @Nullable
    public static byte[] getServerAttribute(String key) {
        return server.getAttribute(key);
    }

    /**
     * 移除服务器全局属性
     */
    public static void removeServerAttribute(String key) {
        server.removeAttribute(key);
    }

    /**
     * 判断是否存在服务器全局属性
     */
    public static boolean hasServerAttribute(String key) {
        return server.hasAttribute(key);
    }
}
