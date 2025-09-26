package net.afyer.afybroker.server.event;

import com.alipay.remoting.Connection;
import net.afyer.afybroker.server.plugin.Event;

/**
 * 当 BrokerClient 初次连接至 BrokerServer 时触发此事件
 *
 * @author Nipuru
 * @since 2022/9/10 17:54
 */
public class ClientConnectEvent extends Event {
    /** 客户端地址 */
    private final String remoteAddress;

    /** 客户端连接 */
    private final Connection connection;

    public ClientConnectEvent(String remoteAddress, Connection connection) {
        this.remoteAddress = remoteAddress;
        this.connection = connection;
    }

    public String getRemoteAddress() {
        return remoteAddress;
    }

    public Connection getConnection() {
        return connection;
    }
}
