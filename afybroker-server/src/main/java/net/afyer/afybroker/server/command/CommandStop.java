package net.afyer.afybroker.server.command;

import net.afyer.afybroker.server.BrokerServer;
import net.afyer.afybroker.server.plugin.Command;

/**
 * @author Nipuru
 * @since 2022/7/31 13:34
 */
public class CommandStop extends Command {

    private final BrokerServer server;

    public CommandStop(BrokerServer server) {
        super("stop");
        this.server = server;
    }

    @Override
    public void execute(String[] args) {
        server.shutdown();
    }
}
