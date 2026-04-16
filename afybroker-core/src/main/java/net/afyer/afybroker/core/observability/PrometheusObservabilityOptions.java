package net.afyer.afybroker.core.observability;

public class PrometheusObservabilityOptions {

    private String host = "0.0.0.0";
    private int port = 9464;

    public String getHost() {
        return host;
    }

    public PrometheusObservabilityOptions setHost(String host) {
        this.host = host;
        return this;
    }

    public int getPort() {
        return port;
    }

    public PrometheusObservabilityOptions setPort(int port) {
        this.port = port;
        return this;
    }
}
