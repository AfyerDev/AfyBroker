package net.afyer.afybroker.server;

import com.alipay.remoting.rpc.RpcServer;
import com.google.common.collect.Lists;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import net.afyer.afybroker.server.command.CommandStop;
import net.afyer.afybroker.server.plugin.Plugin;
import net.afyer.afybroker.server.plugin.PluginManager;
import net.afyer.afybroker.server.proxy.BrokerClientProxyManager;
import net.afyer.afybroker.server.proxy.BrokerPlayerManager;
import net.afyer.afybroker.server.scheduler.BrokerScheduler;
import org.slf4j.Logger;

import java.io.File;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author Nipuru
 * @since 2022/7/29 20:13
 */
@Slf4j
@Getter
@Setter(AccessLevel.PACKAGE)
@FieldDefaults(level = AccessLevel.PRIVATE)
public class BrokerServer {

    /** rpc server */
    RpcServer rpcServer;
    /** broker 端口 */
    int port;
    /** broker 运行状态 */
    boolean start;

    final PluginManager pluginManager;
    final BrokerScheduler scheduler;
    final File pluginsFolder;

    /** 客户端代理 管理器  */
    final BrokerClientProxyManager brokerClientProxyManager;
    /** 玩家代理 管理器  */
    final BrokerPlayerManager brokerPlayerManager;
    final ExecutorService bizThread;

    public BrokerServer() {
        this.pluginManager = new PluginManager(this);
        this.scheduler = new BrokerScheduler(this);
        this.pluginsFolder = new File("plugins");
        this.brokerClientProxyManager = new BrokerClientProxyManager();
        this.brokerPlayerManager = new BrokerPlayerManager();
        this.bizThread = new ThreadPoolExecutor(0, Integer.MAX_VALUE,
                60L, TimeUnit.SECONDS,
                new SynchronousQueue<>(),
                new ThreadFactoryBuilder().setNameFormat("BrokerServer Pool Thread %d").build());

        this.pluginManager.registerCommand(null, new CommandStop(this));
    }

    void initServer() {
        this.rpcServer = new RpcServer(port, true);
        pluginsFolder.mkdirs();
    }

    public void startup() {
        synchronized (this) {
            if (start) {
                log.info("BrokerServer 已经启动");
                return;
            }
            this.start = true;
            log.info("BrokerServer port: [{}] 正在启动", this.port);
            long start = System.currentTimeMillis();
            // 启动 bolt rpc
            this.rpcServer.startup();

            pluginManager.detectPlugins(pluginsFolder);
            pluginManager.loadPlugins();
            pluginManager.enablePlugins();

            log.info("BrokerServer 已启动, 耗时: {}ms", System.currentTimeMillis() - start);
        }
    }

    public void shutdown() {
        synchronized (this) {
            if (!start) {
                log.info("BrokerServer 已经关闭");
                return;
            }
            start = false;
            log.info("BrokerServer 正在关闭");
            new Thread("Shutdown Thread") {
                @Override
                public void run() {
                    log.info("BrokerServer 禁用插件中");
                    for (Plugin plugin : Lists.reverse(new ArrayList<>(pluginManager.getPlugins()))) {
                        try {
                            plugin.onDisable();
                        }
                        catch (Throwable t) {
                            log.error("Exception disabling plugin " + plugin.getDescription().getName(), t);
                        }
                        scheduler.cancel(plugin);
                    }
                    rpcServer.shutdown();
                    bizThread.shutdown();
                    log.info("BrokerServer 已关闭");
                    System.exit(0);
                }
            }.start();
        }
    }

    public static Logger getLogger() {
        return log;
    }

    public static BrokerServerBuilder newBuilder() {
        return new BrokerServerBuilder();
    }

}
