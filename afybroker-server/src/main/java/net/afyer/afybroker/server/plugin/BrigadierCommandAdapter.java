package net.afyer.afybroker.server.plugin;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import net.afyer.afybroker.server.BrokerServer;

import java.util.concurrent.CompletableFuture;

import static com.mojang.brigadier.arguments.StringArgumentType.getString;
import static com.mojang.brigadier.arguments.StringArgumentType.greedyString;

/**
 * Bridges legacy command API to brigadier runtime.
 */
public final class BrigadierCommandAdapter implements BrigadierCommand {

    private final Command legacy;

    public BrigadierCommandAdapter(Command legacy) {
        this.legacy = legacy;
    }

    @Override
    public String getName() {
        return legacy.getName();
    }

    @Override
    public String getUsage() {
        return legacy.getUsage();
    }

    @Override
    public LiteralArgumentBuilder<BrokerServer> createBuilder() {
        LiteralArgumentBuilder<BrokerServer> builder = literal(legacy.getName())
                .executes(context -> {
                    legacy.execute(new String[0]);
                    return 1;
                });

        RequiredArgumentBuilder<BrokerServer, String> argsNode = argument("args", greedyString())
                .executes(context -> {
                    legacy.execute(splitArgs(getString(context, "args"), false));
                    return 1;
                })
                .suggests((context, suggestionsBuilder) -> completeLegacy(legacy, context.getInput(), suggestionsBuilder));

        builder.then(argsNode);
        return builder;
    }

    private static CompletableFuture<com.mojang.brigadier.suggestion.Suggestions> completeLegacy(
            Command command,
            String fullInput,
            SuggestionsBuilder suggestionsBuilder
    ) {
        if (!(command instanceof TabExecutor)) {
            return suggestionsBuilder.buildFuture();
        }

        int firstSpace = fullInput.indexOf(' ');
        String argsLine = (firstSpace < 0) ? "" : fullInput.substring(firstSpace + 1);
        String[] args = splitArgs(argsLine, true);
        for (String suggestion : ((TabExecutor) command).onTabComplete(args)) {
            suggestionsBuilder.suggest(suggestion);
        }
        return suggestionsBuilder.buildFuture();
    }

    private static String[] splitArgs(String value, boolean keepTrailingEmpty) {
        if (value == null || value.isEmpty()) {
            return keepTrailingEmpty ? new String[]{""} : new String[0];
        }
        if (keepTrailingEmpty) {
            return value.split(" ", -1);
        }

        String[] split = value.split(" ", -1);
        int end = split.length;
        while (end > 0 && split[end - 1].isEmpty()) {
            end--;
        }
        if (end == split.length) {
            return split;
        }
        String[] trimmed = new String[end];
        System.arraycopy(split, 0, trimmed, 0, end);
        return trimmed;
    }
}
