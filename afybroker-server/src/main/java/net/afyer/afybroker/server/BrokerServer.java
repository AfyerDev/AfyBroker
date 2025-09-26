package net.afyer.afybroker.server;

import com.alipay.remoting.BizContext;
import com.alipay.remoting.ConnectionEventProcessor;
import com.alipay.remoting.ConnectionEventType;
import com.alipay.remoting.rpc.RpcServer;
import com.alipay.remoting.rpc.protocol.UserProcessor;
import com.alipay.remoting.serialization.SerializerManager;
import com.google.common.collect.Lists;
import jline.console.ConsoleReader;
import net.afyer.afybroker.core.serializer.HessianSerializer;
import net.afyer.afybroker.server.aware.BrokerServerAware;
import net.afyer.afybroker.server.command.*;
import net.afyer.afybroker.server.plugin.BrokerClassLoader;
import net.afyer.afybroker.server.plugin.Plugin;
import net.afyer.afybroker.server.plugin.PluginManager;
import net.afyer.afybroker.server.proxy.*;
import net.afyer.afybroker.server.scheduler.BrokerScheduler;
import net.afyer.afybroker.server.task.PlayerHeartbeatValidateTask;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.UUID;

/**
 * @author Nipuru
 * @since 2022/7/29 20:13
 */
public class BrokerServer {

    private static final Logger LOGGER = LoggerFactory.getLogger(BrokerServer.class);

    /** rpc server */
    private RpcServer rpcServer;
    /** broker 端口 */
    private int port;
    /**
     * broker 运行状态
     */
    private boolean start;

    private final ConsoleReader consoleReader;
    private final PluginManager pluginManager;
    private final BrokerScheduler scheduler;
    private final File pluginsFolder;

    /**
     * 客户端代理 管理器
     */
    private final BrokerClientManager clientManager;
    /**
     * 玩家代理 管理器
     */
    private final BrokerPlayerManager playerManager;
    
    /**
     * 服务注册表
     */
    private final BrokerServiceRegistry serviceRegistry;

    private final PlayerHeartbeatValidateTask playerHeartbeatValidateTask;

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

    void setPort(int port) {
        this.port = port;
    }

    public RpcServer getRpcServer() {
        return rpcServer;
    }

    public int getPort() {
        return port;
    }

    public boolean isStart() {
        return start;
    }

    public ConsoleReader getConsoleReader() {
        return consoleReader;
    }

    public PluginManager getPluginManager() {
        return pluginManager;
    }

    public BrokerScheduler getScheduler() {
        return scheduler;
    }

    public File getPluginsFolder() {
        return pluginsFolder;
    }

    public BrokerClientManager getClientManager() {
        return clientManager;
    }

    public BrokerPlayerManager getPlayerManager() {
        return playerManager;
    }

    public BrokerServiceRegistry getServiceRegistry() {
        return serviceRegistry;
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
                LOGGER.info("Server already started!");
                return;
            }
            this.start = true;
            LOGGER.info("Server port: [{}], Starting", this.port);
            long start = System.currentTimeMillis();

            pluginManager.detectPlugins(pluginsFolder);
            pluginManager.loadPlugins();
            pluginManager.enablePlugins();
            playerHeartbeatValidateTask.start();

            // 启动 bolt rpc
            this.rpcServer.startup();

            LOGGER.info("Done ({}ms)", System.currentTimeMillis() - start);
        }
    }

    public void shutdown() {
        synchronized (this) {
            if (!start) {
                LOGGER.info("Server already closed");
                return;
            }
            start = false;
            LOGGER.info("Stopping the server");
            new Thread("Shutdown Thread") {
                @Override
                public void run() {
                    LOGGER.info("Stopping server");
                    LOGGER.info("Disabling plugins");
                    for (Plugin plugin : Lists.reverse(new ArrayList<>(pluginManager.getPlugins()))) {
                        try {
                            plugin.onDisable();
                        }
                        catch (Throwable t) {
                            LOGGER.error("Exception disabling plugin " + plugin.getDescription().getName(), t);
                        }
                        scheduler.cancel(plugin);
                    }
                    playerHeartbeatValidateTask.cancel();
                    rpcServer.shutdown();
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

    static {
        // 添加默认序列化器
        ClassLoader classLoader = new BrokerClassLoader();
        SerializerManager.addSerializer(SerializerManager.Hessian2, new HessianSerializer(classLoader));
    }

}
