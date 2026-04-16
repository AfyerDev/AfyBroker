package net.afyer.afybroker.server;

import java.io.IOException;

/**
 * @author Nipuru
 * @since 2022/7/29 20:19
 */
public class BootStrap {

    static {
        if (System.getProperty("log4j.skipJansi") == null) {
            System.setProperty("log4j.skipJansi", "false");
        }
        if (System.getProperty("log4j2.disableAnsi") == null) {
            System.setProperty("log4j2.disableAnsi", "false");
        }
    }

    public static void main(String[] args) throws IOException {
        BrokerServer brokerServer = BrokerServer.builder().build();

        Broker.setServer(brokerServer);

        brokerServer.startup();
        brokerServer.runConsoleLoop();
    }
}
