package net.afyer.afybroker.core.message;

import java.io.Serializable;
import java.util.Map;

/**
 * broker-server 发出，由 proxy 类型的服务器处理
 */
public class SyncServerMessage implements Serializable {

    /** 在线的 mc 服务器列表， key:name, value:address */
    private Map<String, String> servers;

    public Map<String, String> getServers() {
        return servers;
    }

    public SyncServerMessage setServers(Map<String, String> servers) {
        this.servers = servers;
        return this;
    }

    @Override
    public String toString() {
        return "SyncServerMessage{" +
                "servers=" + servers +
                '}';
    }
}
