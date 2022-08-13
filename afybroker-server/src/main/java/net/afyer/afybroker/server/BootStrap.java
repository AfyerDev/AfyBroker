package net.afyer.afybroker.server;

import java.io.IOException;

/**
 * @author Nipuru
 * @since 2022/7/29 20:19
 */
public class BootStrap {

    public static void main(String[] args) throws IOException {
        BrokerServer brokerServer = BrokerServer.builder().build();
        brokerServer.startup();

        String line;
        while (brokerServer.isStart() && (line = brokerServer.getConsoleReader().readLine(">")) != null) {
            if (!brokerServer.getPluginManager().dispatchCommand(line)) {
                BrokerServer.getLogger().warn("Command not found.");
            }
        }
    }

}
