package net.afyer.afybroker.server.command;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.afyer.afybroker.server.BrokerServer;
import net.afyer.afybroker.server.plugin.BrigadierCommand;
import net.afyer.afybroker.server.proxy.BrokerClientItem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Nipuru
 * @since 2022/7/31 14:17
 */
public class CommandList implements BrigadierCommand {

    private static final Logger LOGGER = LoggerFactory.getLogger(CommandList.class);

    @Override
    public String getName() {
        return "list";
    }

    @Override
    public String getUsage() {
        return "list|ls - List connected broker clients";
    }

    @Override
    public LiteralArgumentBuilder<BrokerServer> createBuilder() {
        return literal(getName())
                .executes(context -> {
                    BrokerServer server = context.getSource();
                    List<BrokerClientItem> clients = new ArrayList<>(server.getClientManager().list());

                    for (BrokerClientItem client : clients) {
                        LOGGER.info("[{}] {}", client.getType(), client.getName());
                        LOGGER.info("    Address: {}, Tags: {}, Metadata: {}", client.getAddress(), client.getTags(), client.getMetadata());
                    }
                    return 1;
                });
    }
}
