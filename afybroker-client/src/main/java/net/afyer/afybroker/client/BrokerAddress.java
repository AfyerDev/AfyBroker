package net.afyer.afybroker.client;


/**
 * @author Nipuru
 * @since 2022/7/31 16:09
 */
public class BrokerAddress {

    private final String host;
    private final int port;

    public BrokerAddress(String host, int port) {
        this.host = host;
        this.port = port;
    }

    public String getHost() {
        return host;
    }

    public int getPort() {
        return port;
    }

    public String getAddress() {
        return host + ":" + port;
    }

}
