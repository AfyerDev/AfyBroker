package net.afyer.afybroker.server.command;

import com.mojang.brigadier.LiteralMessage;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import net.afyer.afybroker.server.BrokerServer;
import net.afyer.afybroker.server.plugin.BrigadierCommand;
import net.afyer.afybroker.server.plugin.PluginManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

import static com.mojang.brigadier.arguments.StringArgumentType.getString;
import static com.mojang.brigadier.arguments.StringArgumentType.word;

/**
 * @author Nipuru
 * @since 2026/3/3 00:00
 */
public class CommandHelp implements BrigadierCommand {

    private static final Logger LOGGER = LoggerFactory.getLogger(CommandHelp.class);
    private static final DynamicCommandExceptionType COMMAND_NOT_FOUND_EXCEPTION =
            new DynamicCommandExceptionType(name -> new LiteralMessage("No command named '" + name + "' was found."));

    @Override
    public String getName() {
        return "help";
    }

    @Override
    public String getUsage() {
        return "help [command] - Show command list or detail";
    }

    @Override
    public LiteralArgumentBuilder<BrokerServer> createBuilder() {
        return LiteralArgumentBuilder.<BrokerServer>literal(getName())
                .executes(context -> showAll(context.getSource()))
                .then(com.mojang.brigadier.builder.RequiredArgumentBuilder.<BrokerServer, String>argument("command", word())
                        .suggests((context, builder) -> {
                            BrokerServer server = context.getSource();
                            for (PluginManager.CommandMeta meta : server.getPluginManager().getCommandMetas()) {
                                builder.suggest(meta.getName());
                            }
                            return builder.buildFuture();
                        })
                        .executes(context -> showDetail(context.getSource(), getString(context, "command"))));
    }

    private int showDetail(BrokerServer server, String queryText) throws CommandSyntaxException {
        List<PluginManager.CommandMeta> commands = server.getPluginManager().getCommandMetas().stream()
                .sorted(Comparator.comparing(PluginManager.CommandMeta::getName))
                .collect(Collectors.toList());

        String query = queryText.toLowerCase(Locale.ROOT);
        for (PluginManager.CommandMeta command : commands) {
            if (command.getName().equalsIgnoreCase(query)) {
                printCommand(command);
                return 1;
            }
        }

        throw COMMAND_NOT_FOUND_EXCEPTION.create(queryText);
    }

    private int showAll(BrokerServer server) {
        List<PluginManager.CommandMeta> commands = server.getPluginManager().getCommandMetas().stream()
                .sorted(Comparator.comparing(PluginManager.CommandMeta::getName))
                .collect(Collectors.toList());

        LOGGER.info("Available commands:");
        for (PluginManager.CommandMeta command : commands) {
            printCommand(command);
        }
        return 1;
    }

    private void printCommand(PluginManager.CommandMeta command) {
        String usage = command.getUsage() == null || command.getUsage().trim().isEmpty()
                ? command.getName()
                : command.getUsage();

        LOGGER.info(" - {}", command.getName());
        LOGGER.info("    usage: {}", usage);
    }
}
