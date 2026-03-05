package net.afyer.afybroker.server.command;

import com.mojang.brigadier.suggestion.Suggestion;
import net.afyer.afybroker.server.BrokerServer;
import org.jline.reader.Candidate;
import org.jline.reader.Completer;
import org.jline.reader.LineReader;
import org.jline.reader.ParsedLine;

import java.util.List;

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

        List<Suggestion> suggestions = server.getPluginManager().listSuggestion(buffer, line.cursor());
        for (Suggestion suggestion : suggestions) {
            candidates.add(new Candidate(suggestion.getText()));
        }
    }
}
