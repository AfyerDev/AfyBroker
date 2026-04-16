package net.afyer.afybroker.server.plugin;

import com.google.common.base.Preconditions;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import com.google.common.graph.GraphBuilder;
import com.google.common.graph.Graphs;
import com.google.common.graph.MutableGraph;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.ParseResults;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.Suggestion;
import com.mojang.brigadier.suggestion.Suggestions;
import net.afyer.afybroker.server.BrokerServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;
import org.yaml.snakeyaml.introspector.PropertyUtils;

import java.io.File;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.net.URLClassLoader;
import java.util.*;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * @author Nipuru
 * @since 2022/7/31 10:58
 */
public class PluginManager {

    private static final Logger LOGGER = LoggerFactory.getLogger(PluginManager.class);

    private final BrokerServer server;
    private final Yaml yaml;
    private final EventBus eventBus;
    private final Map<String, Plugin> plugins = new LinkedHashMap<>();
    private final MutableGraph<String> dependencyGraph = GraphBuilder.directed().build();
    private final Multimap<Plugin, BrigadierCommand> commandsByPlugin = ArrayListMultimap.create();
    private Map<String, PluginDescription> toLoad = new HashMap<>();
    private final Multimap<Plugin, Listener> listenersByPlugin = ArrayListMultimap.create();
    private CommandDispatcher<BrokerServer> commandDispatcher = new CommandDispatcher<>();

    public PluginManager(BrokerServer server) {
        this.server = server;

        Constructor yamlConstructor = new Constructor();
        PropertyUtils propertyUtils = yamlConstructor.getPropertyUtils();
        propertyUtils.setSkipMissingProperties(true);
        yamlConstructor.setPropertyUtils(propertyUtils);
        yaml = new Yaml(yamlConstructor);

        eventBus = new EventBus();
    }

    public void registerCommand(Plugin plugin, Command command) {
        commandsByPlugin.put(plugin, new BrigadierCommandAdapter(command));
        rebuildCommandDispatcher();
    }

    public void registerCommand(Plugin plugin, BrigadierCommand command) {
        commandsByPlugin.put(plugin, command);
        rebuildCommandDispatcher();
    }


    public boolean dispatchCommand(String commandLine) {
        if (commandLine == null || commandLine.trim().isEmpty()) {
            return false;
        }

        ParseResults<BrokerServer> parseResults = commandDispatcher.parse(commandLine, server);

        try {
            commandDispatcher.execute(parseResults);
            return true;
        } catch (CommandSyntaxException ex) {
            String reason = ex.getRawMessage() == null ? ex.getMessage() : ex.getRawMessage().getString();
            LOGGER.error(reason);
            return false;
        } catch (Exception ex) {
            LOGGER.error("Error in dispatching command", ex);
            return false;
        }
    }

    public List<Suggestion> listSuggestion(String commandLine, int cursor) {
        ParseResults<BrokerServer> parseResults = commandDispatcher.parse(commandLine, server);
        Suggestions suggestions = commandDispatcher.getCompletionSuggestions(parseResults, cursor).join();
        return suggestions.getList();
    }

    public Collection<Plugin> getPlugins() {
        return plugins.values();
    }

    public Plugin getPlugin(String name) {
        return plugins.get(name);
    }

    public void loadPlugins() {
        Map<PluginDescription, Boolean> pluginStatuses = new HashMap<>();
        for (Map.Entry<String, PluginDescription> entry : toLoad.entrySet()) {
            PluginDescription plugin = entry.getValue();
            if (!enablePlugin(pluginStatuses, new Stack<>(), plugin)) {
                LOGGER.warn("Failed to enable {}", entry.getKey());
            }
        }
        toLoad.clear();
        toLoad = null;
    }

    public void enablePlugins() {
        for (Plugin plugin : plugins.values()) {
            try {
                plugin.onEnable();
                LOGGER.info("Enabled plugin {} version {} by {}",
                        plugin.getDescription().getName(),
                        plugin.getDescription().getVersion(),
                        plugin.getDescription().getAuthor());
            } catch (Throwable t) {
                LOGGER.warn("Exception encountered when loading plugin: " + plugin.getDescription().getName(), t);
            }
        }
    }

    private boolean enablePlugin(Map<PluginDescription, Boolean> pluginStatuses, Stack<PluginDescription> dependStack, PluginDescription plugin) {
        if (pluginStatuses.containsKey(plugin)) {
            return pluginStatuses.get(plugin);
        }

        Set<String> dependencies = new HashSet<>();
        dependencies.addAll(plugin.getDepends());
        dependencies.addAll(plugin.getSoftDepends());

        boolean status = true;
        for (String dependName : dependencies) {
            PluginDescription depend = toLoad.get(dependName);
            Boolean dependStatus = (depend != null) ? pluginStatuses.get(depend) : Boolean.FALSE;

            if (dependStatus == null) {
                if (dependStack.contains(depend)) {
                    StringBuilder dependencyGraph = new StringBuilder();
                    for (PluginDescription element : dependStack) {
                        dependencyGraph.append(element.getName()).append(" -> ");
                    }
                    dependencyGraph.append(plugin.getName()).append(" -> ").append(dependName);
                    LOGGER.info("Circular dependency detected: {}", dependencyGraph);
                    status = false;
                } else {
                    dependStack.push(plugin);
                    dependStatus = this.enablePlugin(pluginStatuses, dependStack, depend);
                    dependStack.pop();
                }
            }

            if (dependStatus == Boolean.FALSE && plugin.getDepends().contains(dependName)) {
                LOGGER.warn("{} (required by {}) is unavailable", dependName, plugin.getName());
                status = false;
            }

            dependencyGraph.putEdge(plugin.getName(), dependName);
            if (!status) {
                break;
            }
        }

        if (status) {
            try {
                URLClassLoader loader = new PluginClassloader(server, plugin, plugin.getFile());
                Class<?> main = loader.loadClass(plugin.getMain());
                Plugin clazz = newPluginInstance(main);
                plugins.put(plugin.getName(), clazz);
                clazz.onLoad();
                LOGGER.info("Loaded plugin {} version {} by {}", plugin.getName(), plugin.getVersion(), plugin.getAuthor());
            } catch (Throwable t) {
                LOGGER.warn("Error loading plugin " + plugin.getName(), t);
            }
        }

        pluginStatuses.put(plugin, status);
        return status;
    }

    private static Plugin newPluginInstance(Class<?> main) throws Exception {
        Plugin plugin;

        Field singletonField = getSingletonField(main);
        if (singletonField != null) {
            plugin = (Plugin) singletonField.get(null);
        } else {
            plugin = (Plugin) main.getDeclaredConstructor().newInstance();
        }

        return plugin;
    }

    private static Field getSingletonField(Class<?> clazz) {
        try {
            Field instanceField = clazz.getDeclaredField("INSTANCE");

            if (!Modifier.isStatic(instanceField.getModifiers())) {
                return null;
            }

            if (!Modifier.isFinal(instanceField.getModifiers())) {
                return null;
            }

            if (!instanceField.getType().isAssignableFrom(clazz)) {
                return null;
            }
            instanceField.setAccessible(true);
            return instanceField;
        } catch (NoSuchFieldException e) {
            return null;
        }
    }

    public void detectPlugins(File folder) {
        Preconditions.checkNotNull(folder, "folder");
        Preconditions.checkArgument(folder.isDirectory(), "Must load from a directory");

        for (File file : folder.listFiles()) {
            if (file.isFile() && file.getName().endsWith(".jar")) {
                try (JarFile jar = new JarFile(file)) {
                    JarEntry pdf = jar.getJarEntry("broker.yml");
                    if (pdf == null) {
                        pdf = jar.getJarEntry("plugin.yml");
                    }
                    Preconditions.checkNotNull(pdf, "Plugin must have a plugin.yml or broker.yml");

                    try (InputStream in = jar.getInputStream(pdf)) {
                        PluginDescription desc = yaml.loadAs(in, PluginDescription.class);
                        Preconditions.checkNotNull(desc.getName(), "Plugin from %s has no name", file);
                        Preconditions.checkNotNull(desc.getMain(), "Plugin from %s has no main", file);

                        desc.setFile(file);
                        toLoad.put(desc.getName(), desc);
                    }
                } catch (Exception ex) {
                    LOGGER.warn("Could not load plugin from file " + file, ex);
                }
            }
        }
    }

    public <T extends Event> T callEvent(T event) {
        Preconditions.checkNotNull(event, "event");

        long start = System.nanoTime();
        eventBus.post(event);
        event.postCall();

        long elapsed = System.nanoTime() - start;
        if (elapsed > 250000000) {
            LOGGER.warn("Event {} took {}ms to process!", event, elapsed / 1000000);
        }
        return event;
    }


    public void registerListener(Plugin plugin, Listener listener) {
        eventBus.register(listener);
        listenersByPlugin.put(plugin, listener);
    }

    public void unregisterListener(Listener listener) {
        eventBus.unregister(listener);
        listenersByPlugin.values().remove(listener);
    }

    public void unregisterListeners(Plugin plugin) {
        for (Iterator<Listener> it = listenersByPlugin.get(plugin).iterator(); it.hasNext(); ) {
            eventBus.unregister(it.next());
            it.remove();
        }
    }

    public Collection<CommandMeta> getCommandMetas() {
        Map<String, CommandMeta> commands = new LinkedHashMap<>();

        for (BrigadierCommand command : new LinkedHashSet<>(commandsByPlugin.values())) {
            commands.put(command.getName().toLowerCase(Locale.ROOT), new CommandMeta(
                    command.getName(),
                    command.getUsage()
            ));
        }

        return Collections.unmodifiableCollection(commands.values());
    }

    private void rebuildCommandDispatcher() {
        commandDispatcher = new CommandDispatcher<>();
        Set<String> registeredLiterals = new HashSet<>();

        for (BrigadierCommand command : new LinkedHashSet<>(commandsByPlugin.values())) {
            registerBrigadierCommand(commandDispatcher, registeredLiterals, command);
        }
    }

    private void registerBrigadierCommand(CommandDispatcher<BrokerServer> dispatcher, Set<String> registeredLiterals, BrigadierCommand command) {
        String literal = command.getName().toLowerCase(Locale.ROOT);
        if (!registeredLiterals.add(literal)) {
            LOGGER.warn("Skipping duplicated command literal '{}'", command.getName());
            return;
        }

        LiteralArgumentBuilder<BrokerServer> builder = command.createBuilder();
        com.mojang.brigadier.tree.LiteralCommandNode<BrokerServer> root = dispatcher.register(builder);
    }

    public static final class CommandMeta {
        private final String name;
        private final String usage;

        public CommandMeta(String name, String usage) {
            this.name = name;
            this.usage = usage;
        }

        public String getName() {
            return name;
        }

        public String getUsage() {
            return usage;
        }
    }

    boolean isTransitiveDepend(PluginDescription plugin, PluginDescription depend) {
        Preconditions.checkArgument(plugin != null, "plugin");
        Preconditions.checkArgument(depend != null, "depend");

        if (dependencyGraph.nodes().contains(plugin.getName())) {
            return Graphs.reachableNodes(dependencyGraph, plugin.getName()).contains(depend.getName());
        }
        return false;
    }
}
