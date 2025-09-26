package net.afyer.afybroker.server;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * @author Nipuru
 * @since 2022/7/29 20:19
 */
public class BootStrap {

    private static final Logger LOGGER = LoggerFactory.getLogger(BootStrap.class);

    public static void main(String[] args) throws IOException {
        BrokerServer brokerServer = BrokerServer.builder().build();

        Broker.setServer(brokerServer);

        brokerServer.startup();

        String line;
        while (brokerServer.isStart() && (line = brokerServer.getConsoleReader().readLine()) != null) {
            if (!brokerServer.getPluginManager().dispatchCommand(line)) {
                LOGGER.warn("Command not found.");
            }
        }
    }

}
