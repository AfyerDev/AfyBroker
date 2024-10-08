package net.afyer.afybroker.server.command;

import lombok.extern.slf4j.Slf4j;
import net.afyer.afybroker.server.BrokerServer;
import net.afyer.afybroker.server.plugin.Command;
import net.afyer.afybroker.server.proxy.BrokerClientItem;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Nipuru
 * @since 2022/7/31 14:17
 */
@Slf4j
public class CommandList extends Command {

    private final BrokerServer server;

    public CommandList(BrokerServer server) {
        super("list", "ls");
        this.server = server;
    }

    @Override
    public void execute(String[] args) {
        List<BrokerClientItem> clients = new ArrayList<>(server.getClientManager().list());

        for (BrokerClientItem client : clients) {
            log.info("BrokerClient(type={}, address={}, name={}, tags={})",
                    client.getType(), client.getAddress(), client.getName(), client.getTags());
        }
    }
}
