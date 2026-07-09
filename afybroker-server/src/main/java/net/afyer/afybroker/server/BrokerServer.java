package net.afyer.afybroker.server;

import com.alipay.remoting.BizContext;
import com.alipay.remoting.ConnectionEventProcessor;
import com.alipay.remoting.ConnectionEventType;
import com.alipay.remoting.rpc.RpcServer;
import com.alipay.remoting.rpc.protocol.UserProcessor;
import com.alipay.remoting.serialization.SerializerManager;
import net.afyer.afybroker.core.Attributable;
import net.afyer.afybroker.core.AttributeContainer;
import net.afyer.afybroker.core.interceptor.Interceptor;
import net.afyer.afybroker.core.observability.Observability;
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
import org.jline.reader.EndOfFileException;
import org.jline.reader.LineReader;
import org.jline.reader.LineReaderBuilder;
import org.jline.reader.UserInterruptException;
import org.jline.terminal.Terminal;
import org.jline.terminal.TerminalBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static net.afyer.afybroker.core.util.BoltUtils.checkInterest;

/**
 * @author Nipuru
 * @since 2022/7/29 20:13
 */
public class BrokerServer implements Attributable {

    private static final Logger LOGGER = LoggerFactory.getLogger(BrokerServer.class);

    /**
     * rpc server
     */
    private RpcServer rpcServer;
    /**
     * broker 地址
     */
    private String host;
    /**
     * broker 端口
     */
    private int port;
    /**
     * broker 运行状态
     */
    private boolean start;
    private Observability observability = Observability.NOOP;
    private List<Interceptor> interceptors = Collections.emptyList();
    private final Terminal terminal;
    private final LineReader consoleReader;
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

    /**
     * 服务器全局属性
     */
    private final AttributeContainer attributes = new AttributeContainer();

    BrokerServer() throws IOException {
        this.terminal = TerminalBuilder.builder().system(true).jansi(true).build();
        this.consoleReader = LineReaderBuilder.builder()
                .terminal(this.terminal)
                .completer(new ConsoleCommandCompleter(this))
                .build();
        this.consoleReader.setOpt(LineReader.Option.DISABLE_EVENT_EXPANSION);
        this.pluginManager = new PluginManager(this);
        this.scheduler = new BrokerScheduler(this);
        this.pluginsFolder = new File("plugins");
        this.clientManager = new BrokerClientManager();
        this.playerManager = new BrokerPlayerManager();
        this.serviceRegistry = new BrokerServiceRegistry();
        this.playerHeartbeatValidateTask = new PlayerHeartbeatValidateTask(this);
        this.pluginManager.registerCommand(null, new CommandStop());
        this.pluginManager.registerCommand(null, new CommandList());
        this.pluginManager.registerCommand(null, new CommandListPlayer());
        this.pluginManager.registerCommand(null, new CommandKick());
        this.pluginManager.registerCommand(null, new CommandHelp());
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    void initServer() {
        this.rpcServer = new RpcServer(host, port, true);
        pluginsFolder.mkdirs();
    }

    public void setHost(String host) {
        this.host = host;
    }

    void setPort(int port) {
        this.port = port;
    }

    void setObservability(Observability observability) {
        this.observability = observability;
    }

    void setInterceptors(List<Interceptor> interceptors) {
        this.interceptors = interceptors == null ? Collections.emptyList() : interceptors;
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

    public LineReader getConsoleReader() {
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

    public Observability getObservability() {
        return observability;
    }

    public List<Interceptor> getInterceptors() {
        return interceptors;
    }

    public void registerUserProcessor(UserProcessor<?> processor) {
        aware(processor);
        checkInterest(processor);
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
            long startMillis = System.currentTimeMillis();

            pluginManager.detectPlugins(pluginsFolder);
            pluginManager.loadPlugins();
            pluginManager.enablePlugins();
            playerHeartbeatValidateTask.start();

            // 启动 bolt rpc
            this.rpcServer.startup();
            LOGGER.info("Done ({}ms)", System.currentTimeMillis() - startMillis);
        }
    }

    public void shutdown() {
        shutdownGracefully();
        System.exit(0);
    }

    public void shutdownGracefully() {
        synchronized (this) {
            if (!start) {
                logInfo("Server already closed");
                return;
            }
            start = false;
            logInfo("Stopping the server");
            logInfo("Disabling plugins");
            List<Plugin> plugins = new ArrayList<>(pluginManager.getPlugins());
            Collections.reverse(plugins);
            for (Plugin plugin : plugins) {
                try {
                    plugin.onDisable();
                } catch (Throwable t) {
                    logError("Exception disabling plugin " + plugin.getDescription().getName(), t);
                }
                try {
                    scheduler.cancel(plugin);
                } catch (Throwable t) {
                    logError("Exception cancelling tasks for plugin " + plugin.getDescription().getName(), t);
                }
            }
            try {
                playerHeartbeatValidateTask.cancel();
            } catch (Throwable t) {
                logError("Exception cancelling player heartbeat validation", t);
            }
            try {
                rpcServer.shutdown();
            } catch (Throwable t) {
                logError("Exception shutting down rpc server", t);
            }
            try {
                terminal.close();
            } catch (Throwable t) {
                logError("Exception closing terminal", t);
            }
            try {
                observability.close();
            } catch (Throwable t) {
                logError("Exception closing observability", t);
            }
        }
    }

    private void logInfo(String message) {
        try {
            LOGGER.info(message);
        } catch (Throwable ignored) {
        }
    }

    private void logError(String message, Throwable throwable) {
        try {
            LOGGER.error(message, throwable);
        } catch (Throwable ignored) {
        }
    }

    public void runConsoleLoop() {
        while (isStart()) {
            String line;
            try {
                line = this.consoleReader.readLine(">");
            } catch (UserInterruptException e) {
                this.shutdown();
                break;
            } catch (EndOfFileException e) {
                break;
            }

            if (line == null) {
                break;
            }

            this.pluginManager.dispatchCommand(line);
        }
    }

    public void aware(Object object) {
        if (object instanceof BrokerServerAware) {
            ((BrokerServerAware) object).setBrokerServer(this);
        }
    }

    @Override
    public AttributeContainer getAttributeContainer() {
        return attributes;
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
