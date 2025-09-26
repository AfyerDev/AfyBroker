package net.afyer.afybroker.server.command;

import net.afyer.afybroker.server.BrokerServer;
import net.afyer.afybroker.server.plugin.Command;
import net.afyer.afybroker.server.proxy.BrokerClientItem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Nipuru
 * @since 2022/7/31 14:17
 */
public class CommandList extends Command {

    private static final Logger LOGGER = LoggerFactory.getLogger(CommandList.class);

    private final BrokerServer server;

    public CommandList(BrokerServer server) {
        super("list", "ls");
        this.server = server;
    }

    @Override
    public void execute(String[] args) {
        List<BrokerClientItem> clients = new ArrayList<>(server.getClientManager().list());

        for (BrokerClientItem client : clients) {
            LOGGER.info("[{}] {}", client.getType(), client.getName());
            LOGGER.info("    Address: {}, Tags: {}, Metadata: {}", client.getAddress(), client.getTags(), client.getMetadata());
        }
    }
}
