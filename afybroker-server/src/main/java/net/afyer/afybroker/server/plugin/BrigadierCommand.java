package net.afyer.afybroker.server.plugin;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.afyer.afybroker.server.BrokerServer;

/**
 * Brigadier based command contract.
 */
public interface BrigadierCommand {

    String getName();

    default String getUsage() {
        return getName();
    }

    LiteralArgumentBuilder<BrokerServer> createBuilder();
}
