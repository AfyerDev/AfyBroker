package net.afyer.afybroker.server.command;

import net.afyer.afybroker.server.BrokerServer;
import net.afyer.afybroker.server.plugin.Command;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author Nipuru
 * @since 2026/3/3 00:00
 */
public class CommandHelp extends Command {

    private static final Logger LOGGER = LoggerFactory.getLogger(CommandHelp.class);

    private final BrokerServer server;

    public CommandHelp(BrokerServer server) {
        super("help", "h");
        setUsage("help [command] - Show command list or detail");
        this.server = server;
    }

    @Override
    public void execute(String[] args) {
        List<Command> commands = server.getPluginManager().getCommands().stream()
                .map(Map.Entry::getValue)
                .distinct()
                .sorted(Comparator.comparing(Command::getName))
                .collect(Collectors.toList());

        if (args.length > 0) {
            String query = args[0].toLowerCase(Locale.ROOT);
            for (Command command : commands) {
                if (command.getName().equalsIgnoreCase(query)) {
                    printCommand(command);
                    return;
                }
                for (String alias : command.getAliases()) {
                    if (alias.equalsIgnoreCase(query)) {
                        printCommand(command);
                        return;
                    }
                }
            }
            LOGGER.info("No command named '{}' was found.", args[0]);
            return;
        }

        LOGGER.info("Available commands:");
        for (Command command : commands) {
            printCommand(command);
        }
    }

    private void printCommand(Command command) {
        String[] aliases = command.getAliases();
        String usage = command.getUsage() == null || command.getUsage().trim().isEmpty()
                ? command.getName()
                : command.getUsage();

        if (aliases.length == 0) {
            LOGGER.info(" - {}", command.getName());
        } else {
            LOGGER.info(" - {} ({})", command.getName(), String.join(", ", aliases));
        }
        LOGGER.info("    usage: {}", usage);
    }
}
