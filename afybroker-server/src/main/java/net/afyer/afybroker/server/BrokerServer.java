package net.afyer.afybroker.server;

import com.alipay.remoting.BizContext;
import com.alipay.remoting.ConnectionEventProcessor;
import com.alipay.remoting.ConnectionEventType;
import com.alipay.remoting.rpc.RpcServer;
import com.alipay.remoting.rpc.protocol.UserProcessor;
import com.google.common.collect.Lists;
import jline.console.ConsoleReader;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import net.afyer.afybroker.server.aware.BrokerServerAware;
import net.afyer.afybroker.server.command.*;
import net.afyer.afybroker.server.plugin.Plugin;
import net.afyer.afybroker.server.plugin.PluginManager;
import net.afyer.afybroker.server.proxy.BrokerClientItem;
import net.afyer.afybroker.server.proxy.BrokerClientManager;
import net.afyer.afybroker.server.proxy.BrokerPlayer;
import net.afyer.afybroker.server.proxy.BrokerPlayerManager;
import net.afyer.afybroker.server.proxy.BrokerServiceRegistry;
import net.afyer.afybroker.server.scheduler.BrokerScheduler;
import net.afyer.afybroker.server.task.PlayerHeartbeatValidateTask;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.UUID;
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
    /**
     * broker 运行状态
     */
    boolean start;
    /**
     * 事务线程池
     */
    ExecutorService bizThread;

    final ConsoleReader consoleReader;
    final PluginManager pluginManager;
    final BrokerScheduler scheduler;
    final File pluginsFolder;

    /**
     * 客户端代理 管理器
     */
    final BrokerClientManager clientManager;
    /**
     * 玩家代理 管理器
     */
    final BrokerPlayerManager playerManager;
    
    /**
     * 服务注册表
     */
    final BrokerServiceRegistry serviceRegistry;

    final PlayerHeartbeatValidateTask playerHeartbeatValidateTask;


    BrokerServer() throws IOException {
        this.consoleReader = new ConsoleReader();
        this.consoleReader.setExpandEvents(false);
        this.consoleReader.addCompleter(new ConsoleCommandCompleter(this));
        this.pluginManager = new PluginManager(this);
        this.scheduler = new BrokerScheduler(this);
        this.pluginsFolder = new File("plugins");
        this.clientManager = new BrokerClientManager();
        this.playerManager = new BrokerPlayerManager();
        this.serviceRegistry = new BrokerServiceRegistry();
        this.playerHeartbeatValidateTask = new PlayerHeartbeatValidateTask(this);
        this.pluginManager.registerCommand(null, new CommandStop(this));
        this.pluginManager.registerCommand(null, new CommandList(this));
        this.pluginManager.registerCommand(null, new CommandListPlayer(this));
        this.pluginManager.registerCommand(null, new CommandKick(this));
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    void initServer() {
        this.rpcServer = new RpcServer(port, true);
        pluginsFolder.mkdirs();
    }

    public void registerUserProcessor(UserProcessor<?> processor) {
        aware(processor);
        rpcServer.registerUserProcessor(processor);
    }
    public void addConnectionEventProcessor(ConnectionEventType type, ConnectionEventProcessor processor) {
        aware(processor);
        rpcServer.addConnectionEventProcessor(type, processor);
    }

    @Nullable
    public BrokerPlayer getPlayer(UUID uuid) {
        return playerManager.getPlayer(uuid);
    }

    @Nullable
    public BrokerPlayer getPlayer(String name) {
        return playerManager.getPlayer(name);
    }

    @Nullable
    public BrokerClientItem getClient(String name) {
        return clientManager.getByName(name);
    }

    @Nullable
    public BrokerClientItem getClient(BizContext bizContext) {
        return clientManager.getByAddress(bizContext.getRemoteAddress());
    }

    public void startup() {
        synchronized (this) {
            if (start) {
                log.info("Server already started!");
                return;
            }
            this.start = true;
            log.info("Server port: [{}], Starting", this.port);
            long start = System.currentTimeMillis();

            pluginManager.detectPlugins(pluginsFolder);
            pluginManager.loadPlugins();
            pluginManager.enablePlugins();
            playerHeartbeatValidateTask.start();

            // 启动 bolt rpc
            this.rpcServer.startup();

            log.info("Done ({}ms)", System.currentTimeMillis() - start);
        }
    }

    public void shutdown() {
        synchronized (this) {
            if (!start) {
                log.info("Server already closed");
                return;
            }
            start = false;
            log.info("Stopping the server");
            new Thread("Shutdown Thread") {
                @Override
                public void run() {
                    log.info("Stopping server");
                    log.info("Disabling plugins");
                    for (Plugin plugin : Lists.reverse(new ArrayList<>(pluginManager.getPlugins()))) {
                        try {
                            plugin.onDisable();
                        }
                        catch (Throwable t) {
                            log.error("Exception disabling plugin " + plugin.getDescription().getName(), t);
                        }
                        scheduler.cancel(plugin);
                    }
                    playerHeartbeatValidateTask.cancel();
                    rpcServer.shutdown();
                    bizThread.shutdown();
                    System.exit(0);
                }
            }.start();
        }
    }

    public void aware(Object object) {
        if (object instanceof BrokerServerAware) {
            ((BrokerServerAware) object).setBrokerServer(this);
        }
    }

    public static BrokerServerBuilder builder() {
        return new BrokerServerBuilder();
    }

}
