package net.afyer.afybroker.server.event;

import net.afyer.afybroker.server.plugin.Event;

import java.util.Set;

/**
 * 当BrokerClient与BrokerServer断开连接时触发此事件
 * @author Nipuru
 * @since 2022/9/10 17:55
 */
public class ClientCloseEvent extends Event {

    /** 客户端地址 */
    private final String remoteAddress;

    /** 客户端名称 */
    private final String name;

    /** 客户端标签 */
    private final Set<String> tags;

    /** 客户端类型 */
    private final String type;

    public ClientCloseEvent(String remoteAddress, String name, Set<String> tags, String type) {
        this.remoteAddress = remoteAddress;
        this.name = name;
        this.tags = tags;
        this.type = type;
    }

    public String getRemoteAddress() {
        return remoteAddress;
    }

    public String getName() {
        return name;
    }

    public Set<String> getTags() {
        return tags;
    }

    public String getType() {
        return type;
    }

}
