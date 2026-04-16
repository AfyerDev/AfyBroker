package net.afyer.afybroker.server.plugin;

import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import net.afyer.afybroker.server.BrokerServer;

/**
 * Brigadier based command contract.
 */
public interface BrigadierCommand {

    default LiteralArgumentBuilder<BrokerServer> literal(String name) {
        return LiteralArgumentBuilder.literal(name);
    }

    default <T> RequiredArgumentBuilder<BrokerServer, T> argument(String name, ArgumentType<T> type) {
        return RequiredArgumentBuilder.argument(name, type);
    }

    String getName();

    default String getUsage() {
        return getName();
    }

    LiteralArgumentBuilder<BrokerServer> createBuilder();
}
