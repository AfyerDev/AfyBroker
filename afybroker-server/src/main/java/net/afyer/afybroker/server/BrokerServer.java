package net.afyer.afybroker.server;

import com.alipay.remoting.ConnectionEventProcessor;
import com.alipay.remoting.ConnectionEventType;
import com.alipay.remoting.rpc.RpcServer;
import com.alipay.remoting.rpc.protocol.UserProcessor;
import com.google.common.collect.Lists;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import net.afyer.afybroker.server.aware.BrokerServerAware;
import net.afyer.afybroker.server.command.CommandList;
import net.afyer.afybroker.server.command.CommandListPlayer;
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
    /** 事务线程池 */
    ExecutorService bizThread;

    final PluginManager pluginManager;
    final BrokerScheduler scheduler;
    final File pluginsFolder;

    /** 客户端代理 管理器  */
    final BrokerClientProxyManager brokerClientProxyManager;
    /** 玩家代理 管理器  */
    final BrokerPlayerManager brokerPlayerManager;


    BrokerServer() {
        this.pluginManager = new PluginManager(this);
        this.scheduler = new BrokerScheduler(this);
        this.pluginsFolder = new File("plugins");
        this.brokerClientProxyManager = new BrokerClientProxyManager();
        this.brokerPlayerManager = new BrokerPlayerManager();
        this.pluginManager.registerCommand(null, new CommandStop(this));
        this.pluginManager.registerCommand(null, new CommandList(this));
        this.pluginManager.registerCommand(null, new CommandListPlayer(this));
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    void initServer() {
        this.rpcServer = new RpcServer(port, true);
        pluginsFolder.mkdirs();
    }

    public void registerUserProcessor(UserProcessor<?> processor){
        aware(processor);
        rpcServer.registerUserProcessor(processor);
    }
    public void addConnectionEventProcessor(ConnectionEventType type, ConnectionEventProcessor processor) {
        aware(processor);
        rpcServer.addConnectionEventProcessor(type, processor);
    }

    public void startup() {
        synchronized (this) {
            if (start) {
                log.info("BrokerServer already started!");
                return;
            }
            this.start = true;
            log.info("BrokerServer port: [{}], Starting", this.port);
            long start = System.currentTimeMillis();
            // 启动 bolt rpc
            this.rpcServer.startup();

            pluginManager.detectPlugins(pluginsFolder);
            pluginManager.loadPlugins();
            pluginManager.enablePlugins();

            log.info("Done ({}ms)", System.currentTimeMillis() - start);
        }
    }

    public void shutdown() {
        synchronized (this) {
            if (!start) {
                log.info("BrokerServer already closed");
                return;
            }
            start = false;
            log.info("Stopping the server");
            new Thread("Shutdown Thread") {
                @Override
                public void run() {
                    log.info("Stopping server");
                    log.info("BrokerServer disabling plugins");
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
                    System.exit(0);
                }
            }.start();
        }
    }

    public void aware(Object object) {
        if (object instanceof BrokerServerAware brokerServerAware) {
            brokerServerAware.setBrokerServer(this);
        }
    }

    public static Logger getLogger() {
        return log;
    }

    public static BrokerServerBuilder newBuilder() {
        return new BrokerServerBuilder();
    }

}
