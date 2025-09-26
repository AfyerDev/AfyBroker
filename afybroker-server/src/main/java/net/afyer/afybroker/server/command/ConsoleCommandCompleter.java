package net.afyer.afybroker.server.command;

import jline.console.completer.Completer;
import net.afyer.afybroker.server.BrokerServer;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author Nipuru
 * @since 2022/8/6 8:24
 */
public class ConsoleCommandCompleter implements Completer {

    private final BrokerServer server;

    public ConsoleCommandCompleter(BrokerServer server) {
        this.server = server;
    }

    @Override
    public int complete(String buffer, int cursor, List<CharSequence> candidates) {
        int lastSpace = buffer.lastIndexOf(' ');
        if (lastSpace == -1) {
            String lowerCase = buffer.toLowerCase(Locale.ROOT);
            candidates.addAll(server.getPluginManager().getCommands().stream()
                    .map(Map.Entry::getKey)
                    .filter((name) -> name.toLowerCase(Locale.ROOT).startsWith(lowerCase))
                    .collect(Collectors.toList()));
        } else {
            List<String> suggestions = new ArrayList<>();
            server.getPluginManager().dispatchCommand(buffer, suggestions);
            candidates.addAll(suggestions);
        }

        return (lastSpace == -1) ? cursor - buffer.length() : cursor - (buffer.length() - lastSpace - 1);
    }
}
