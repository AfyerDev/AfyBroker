package net.afyer.afybroker.server.command;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.afyer.afybroker.server.BrokerServer;
import net.afyer.afybroker.server.plugin.BrigadierCommand;

/**
 * @author Nipuru
 * @since 2022/7/31 13:34
 */
public class CommandStop implements BrigadierCommand {

    @Override
    public String getName() {
        return "stop";
    }

    @Override
    public String getUsage() {
        return "stop - Gracefully stop the broker server";
    }

    @Override
    public LiteralArgumentBuilder<BrokerServer> createBuilder() {
        return literal(getName())
                .executes(context -> {
                    context.getSource().shutdown();
                    return 1;
                });
    }
}
