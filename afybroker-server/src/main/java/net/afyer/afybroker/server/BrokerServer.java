package net.afyer.afybroker.server;

import com.alipay.remoting.BizContext;
import com.alipay.remoting.ConnectionEventProcessor;
import com.alipay.remoting.ConnectionEventType;
import com.alipay.remoting.rpc.RpcServer;
import com.alipay.remoting.rpc.protocol.UserProcessor;
import com.alipay.remoting.serialization.SerializerManager;
import com.google.common.collect.Lists;
import net.afyer.afybroker.core.Attributable;
import net.afyer.afybroker.core.AttributeContainer;
import net.afyer.afybroker.core.observability.LifecycleState;
import net.afyer.afybroker.core.observability.Observability;
import net.afyer.afybroker.core.observability.PlayerEventType;
import net.afyer.afybroker.core.observability.PlayerObservation;
import net.afyer.afybroker.core.serializer.HessianSerializer;
import net.afyer.afybroker.server.aware.BrokerServerAware;
import net.afyer.afybroker.server.command.CommandHelp;
import net.afyer.afybroker.server.command.CommandKick;
import net.afyer.afybroker.server.command.CommandList;
import net.afyer.afybroker.server.command.CommandListPlayer;
import net.afyer.afybroker.server.command.CommandStop;
import net.afyer.afybroker.server.command.ConsoleCommandCompleter;
import net.afyer.afybroker.server.plugin.BrokerClassLoader;
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
import java.util.UUID;

public class BrokerServer implements Attributable {

    private static final Logger LOGGER = LoggerFactory.getLogger(BrokerServer.class);

    private RpcServer rpcServer;
    private int port;
    private boolean start;
    private Observability observability = Observability.NOOP;
    private final Terminal terminal;
    private final LineReader consoleReader;
    private final PluginManager pluginManager;
    private final BrokerScheduler scheduler;
    private final File pluginsFolder;
    private final BrokerClientManager clientManager;
    private final BrokerPlayerManager playerManager;
    private final BrokerServiceRegistry serviceRegistry;
    private final PlayerHeartbeatValidateTask playerHeartbeatValidateTask;
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
        this.rpcServer = new RpcServer(port, true);
        pluginsFolder.mkdirs();
    }

    void setPort(int port) {
        this.port = port;
    }

    void setObservability(Observability observability) {
        this.observability = observability;
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
            observability.onLifecycle(LifecycleState.STARTING);
            this.start = true;
            LOGGER.info("Server port: [{}], Starting", this.port);
            long startMillis = System.currentTimeMillis();

            try {
                pluginManager.detectPlugins(pluginsFolder);
                pluginManager.loadPlugins();
                pluginManager.enablePlugins();
                playerHeartbeatValidateTask.start();
                this.rpcServer.startup();
                recordOnlinePlayers(playerManager.size());
                observability.onLifecycle(LifecycleState.STARTED);
                LOGGER.info("Done ({}ms)", System.currentTimeMillis() - startMillis);
            } catch (RuntimeException e) {
                observability.onLifecycle(LifecycleState.START_FAILED);
                throw e;
            }
        }
    }

    public void shutdown() {
        synchronized (this) {
            if (!start) {
                LOGGER.info("Server already closed");
                return;
            }
            start = false;
            observability.onLifecycle(LifecycleState.STOPPING);
            LOGGER.info("Stopping the server");
            new Thread("Shutdown Thread") {
                @Override
                public void run() {
                    LOGGER.info("Stopping server");
                    LOGGER.info("Disabling plugins");
                    for (Plugin plugin : Lists.reverse(new ArrayList<>(pluginManager.getPlugins()))) {
                        try {
                            plugin.onDisable();
                        } catch (Throwable t) {
                            LOGGER.error("Exception disabling plugin " + plugin.getDescription().getName(), t);
                        }
                        scheduler.cancel(plugin);
                    }
                    playerHeartbeatValidateTask.cancel();
                    rpcServer.shutdown();
                    try {
                        terminal.close();
                    } catch (IOException ignored) {
                    }
                    observability.onLifecycle(LifecycleState.STOPPED);
                    observability.close();
                    System.exit(0);
                }
            }.start();
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

    public void recordPlayerEvent(PlayerEventType eventType, int onlinePlayers) {
        observability.onPlayer(new PlayerObservation(eventType, onlinePlayers));
    }

    public void recordOnlinePlayers(int onlinePlayers) {
        observability.onPlayer(new PlayerObservation(null, onlinePlayers));
    }

    @Override
    public AttributeContainer getAttributeContainer() {
        return attributes;
    }

    public static BrokerServerBuilder builder() {
        return new BrokerServerBuilder();
    }

    static {
        ClassLoader classLoader = new BrokerClassLoader();
        SerializerManager.addSerializer(SerializerManager.Hessian2, new HessianSerializer(classLoader));
    }
}
