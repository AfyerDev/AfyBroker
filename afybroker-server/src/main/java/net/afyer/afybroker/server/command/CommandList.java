package net.afyer.afybroker.server.command;

import net.afyer.afybroker.server.BrokerServer;
import net.afyer.afybroker.server.plugin.Command;
import net.afyer.afybroker.server.proxy.BrokerClientProxy;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Nipuru
 * @since 2022/7/31 14:17
 */
public class CommandList extends Command {

    private final BrokerServer server;

    public CommandList(BrokerServer server) {
        super("list", "ls");
        this.server = server;
    }

    @Override
    public void execute(String[] args) {
        List<BrokerClientProxy> clients = new ArrayList<>(server.getBrokerClientProxyManager().list());

        for (BrokerClientProxy client : clients) {
            BrokerServer.getLogger().info("BrokerClient(type={}, address={}, name={}, tags={})",
                    client.getType(), client.getAddress(), client.getName(), client.getTags());
        }
    }
}
