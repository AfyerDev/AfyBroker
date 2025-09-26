package net.afyer.afybroker.core;

import net.afyer.afybroker.core.message.BrokerClientInfoMessage;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author Nipuru
 * @since 2022/12/23 10:11
 */
public class BrokerClientInfo {

    /** 客户端名称(唯一标识) */
    private final String name;
    /** 客户端标签 */
    private final Set<String> tags;
    /** 客户端类型 */
    private final String type;
    /** 客户端地址 */
    private final String address;
    /** 客户端元数据 */
    private final Map<String, String> metadata;
    /** 客户端服务列表 */
    private final List<BrokerServiceDescriptor> services;

    public BrokerClientInfo(String name, Set<String> tags, String type, String address, Map<String, String> metadata, List<BrokerServiceDescriptor> services) {
        this.name = name;
        this.tags = tags;
        this.type = type;
        this.address = address;
        this.metadata = metadata;
        this.services = services;
    }

    public String getName() {
        return name;
    }

    public Set<String> getTags() {
        return Collections.unmodifiableSet(tags);
    }

    public String getType() {
        return type;
    }

    public String getAddress() {
        return address;
    }

    public Map<String, String> getMetadata() {
        return Collections.unmodifiableMap(metadata);
    }

    public List<BrokerServiceDescriptor> getServices() {
        return services;
    }

    @Nullable
    public String getMetadata(String key) {
        return metadata.get(key);
    }

    public boolean hasTag(String tag) {
        return tags.contains(tag);
    }

    public boolean hasAnyTags(String... tags) {
        for (String tag : tags) {
            if (this.tags.contains(tag)) {
                return true;
            }
        }
        return false;
    }

    public boolean hasAnyTags(Iterable<String> tags) {
        for (String tag : tags) {
            if (this.tags.contains(tag)) {
                return true;
            }
        }
        return false;
    }

    public boolean hasAllTags(String... tags) {
        for (String tag : tags) {
            if (!this.tags.contains(tag)) {
                return false;
            }
        }
        return true;
    }

    public boolean hasAllTags(Iterable<String> tags) {
        for (String tag : tags) {
            if (!this.tags.contains(tag)) {
                return false;
            }
        }
        return true;
    }

    public BrokerClientInfoMessage toMessage() {
        return new BrokerClientInfoMessage()
                .setName(name)
                .setTags(tags)
                .setType(type)
                .setAddress(address)
                .setMetadata(metadata)
                .setServices(services);
    }
}
