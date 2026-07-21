package net.afyer.afybroker.core.message;

import java.io.Serializable;

/**
 * 玩家连接至 proxy 的 broker 处理结果
 *
 * @author Nipuru
 * @since 2026/7/21 10:32
 */
public class PlayerProxyConnectResult implements Serializable {
    private static final long serialVersionUID = -5698949365033504829L;

    private boolean success;
    private String serverName;

    public boolean isSuccess() {
        return success;
    }

    public PlayerProxyConnectResult setSuccess(boolean success) {
        this.success = success;
        return this;
    }

    public String getServerName() {
        return serverName;
    }

    public PlayerProxyConnectResult setServerName(String serverName) {
        this.serverName = serverName;
        return this;
    }

    @Override
    public String toString() {
        return "PlayerProxyConnectResult{" +
                "success=" + success +
                ", serverName='" + serverName + '\'' +
                '}';
    }
}
