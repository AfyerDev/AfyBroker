package net.afyer.afybroker.server;

import net.afyer.afybroker.core.observability.PrometheusObservabilityOptions;

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
        BrokerServerBuilder builder = BrokerServer.builder();
        if (Boolean.parseBoolean(System.getProperty("afybroker.observability.prometheus.enabled", "false"))) {
            builder.enablePrometheus(new PrometheusObservabilityOptions()
                    .setHost(System.getProperty("afybroker.observability.prometheus.host", "0.0.0.0"))
                    .setPort(Integer.parseInt(System.getProperty("afybroker.observability.prometheus.port", "9464"))));
        }
        BrokerServer brokerServer = builder.build();

        Broker.setServer(brokerServer);

        brokerServer.startup();
        brokerServer.runConsoleLoop();
    }

}
