package net.afyer.afybroker.server;

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;

/**
 * @author Nipuru
 * @since 2022/7/29 20:19
 */
@Slf4j
public class BootStrap {

    public static void main(String[] args) throws IOException {
        BrokerServer brokerServer = BrokerServer.builder().build();

        Broker.setServer(brokerServer);

        brokerServer.startup();

        String line;
        while (brokerServer.isStart() && (line = brokerServer.getConsoleReader().readLine()) != null) {
            if (!brokerServer.getPluginManager().dispatchCommand(line)) {
                log.warn("Command not found.");
            }
        }
    }

}
