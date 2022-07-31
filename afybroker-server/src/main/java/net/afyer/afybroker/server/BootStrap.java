package net.afyer.afybroker.server;

import java.util.Scanner;

/**
 * @author Nipuru
 * @since 2022/7/29 20:19
 */
public class BootStrap {

    public static void main(String[] args) {
        BrokerServer brokerServer = BrokerServer.newBuilder().build();
        brokerServer.startup();

        Scanner scanner = new Scanner(System.in);

        String line;
        while (brokerServer.isStart() && (line = scanner.nextLine()) != null) {
            if (!brokerServer.getPluginManager().dispatchCommand(line)) {
                BrokerServer.getLogger().warn("Command not found.");
            }
        }
    }

}
