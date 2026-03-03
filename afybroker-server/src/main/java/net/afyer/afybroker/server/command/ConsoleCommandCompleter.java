package net.afyer.afybroker.server.command;

import net.afyer.afybroker.server.BrokerServer;
import org.jline.reader.Candidate;
import org.jline.reader.Completer;
import org.jline.reader.LineReader;
import org.jline.reader.ParsedLine;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

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
    public void complete(LineReader reader, ParsedLine line, List<Candidate> candidates) {
        String buffer = line.line();
        if (buffer == null) {
            buffer = "";
        }

        int lastSpace = buffer.lastIndexOf(' ');
        if (lastSpace == -1) {
            String lowerCase = buffer.toLowerCase(Locale.ROOT);
            server.getPluginManager().getCommands().stream()
                    .map(Map.Entry::getKey)
                    .filter((name) -> name.toLowerCase(Locale.ROOT).startsWith(lowerCase))
                    .map(Candidate::new)
                    .forEach(candidates::add);
        } else {
            List<String> suggestions = new ArrayList<>();
            server.getPluginManager().dispatchCommand(buffer, suggestions);
            suggestions.stream().map(Candidate::new).forEach(candidates::add);
        }
    }
}
