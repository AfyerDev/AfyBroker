package net.afyer.afybroker.client;

/**
 * @author Nipuru
 * @since 2022/7/31 16:09
 */
public record BrokerAddress(String host, int port) {

    public String getAddress() {
        return host + ":" + port;
    }

}
