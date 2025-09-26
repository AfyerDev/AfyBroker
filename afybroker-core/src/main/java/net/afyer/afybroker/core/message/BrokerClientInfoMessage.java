package net.afyer.afybroker.core.message;

import net.afyer.afybroker.core.BrokerClientInfo;
import net.afyer.afybroker.core.BrokerServiceDescriptor;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author Nipuru
 * @since 2022/7/30 16:25
 */
public class BrokerClientInfoMessage implements Serializable {
    private static final long serialVersionUID = 5964124139341528361L;

    /** 客户端名称(唯一标识) */
    private String name;
    /** 客户端标签 */
    private Set<String> tags;
    /** 客户端类型 */
    private String type;
    /** 服务器/客户端 地址 */
    private String address;
    /** 客户端元数据 */
    private Map<String, String> metadata;
    /** 客户端服务列表 */
    private List<BrokerServiceDescriptor> services;

    public String getName() {
        return name;
    }

    public BrokerClientInfoMessage setName(String name) {
        this.name = name;
        return this;
    }

    public Set<String> getTags() {
        return tags;
    }

    public BrokerClientInfoMessage setTags(Set<String> tags) {
        this.tags = tags;
        return this;
    }

    public String getType() {
        return type;
    }

    public BrokerClientInfoMessage setType(String type) {
        this.type = type;
        return this;
    }

    public String getAddress() {
        return address;
    }

    public BrokerClientInfoMessage setAddress(String address) {
        this.address = address;
        return this;
    }

    public Map<String, String> getMetadata() {
        return metadata;
    }

    public BrokerClientInfoMessage setMetadata(Map<String, String> metadata) {
        this.metadata = metadata;
        return this;
    }

    public List<BrokerServiceDescriptor> getServices() {
        return services;
    }

    public BrokerClientInfoMessage setServices(List<BrokerServiceDescriptor> services) {
        this.services = services;
        return this;
    }

    public BrokerClientInfo build() {
        return new BrokerClientInfo(name, tags, type, address, metadata,  services);
    }

    @Override
    public String toString() {
        return "BrokerClientInfoMessage{" +
                "name='" + name + '\'' +
                ", tags=" + tags +
                ", type='" + type + '\'' +
                ", address='" + address + '\'' +
                ", metadata=" + metadata +
                ", services=" + services +
                '}';
    }
}
