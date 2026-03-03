package net.afyer.afybroker.server;

import org.jline.reader.EndOfFileException;
import org.jline.reader.UserInterruptException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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

    private static final Logger LOGGER = LoggerFactory.getLogger(BootStrap.class);

    public static void main(String[] args) throws IOException {
        BrokerServer brokerServer = BrokerServer.builder().build();

        Broker.setServer(brokerServer);

        brokerServer.startup();

        while (brokerServer.isStart()) {
            String line;
            try {
                line = brokerServer.getConsoleReader().readLine(">");
            } catch (UserInterruptException e) {
                brokerServer.shutdown();
                break;
            } catch (EndOfFileException e) {
                break;
            }

            if (line == null) {
                break;
            }

            if (!brokerServer.getPluginManager().dispatchCommand(line)) {
                LOGGER.warn("Command not found.");
            }
        }
    }

}
