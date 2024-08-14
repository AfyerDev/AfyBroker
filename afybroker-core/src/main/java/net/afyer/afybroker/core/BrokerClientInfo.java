package net.afyer.afybroker.core;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import net.afyer.afybroker.core.message.BrokerClientInfoMessage;

import java.util.Collections;
import java.util.Set;

import com.google.common.collect.Sets;

/**
 * @author Nipuru
 * @since 2022/12/23 10:11
 */
@Getter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class BrokerClientInfo {

    /** 客户端名称(唯一标识) */
    final String name;
    /** 客户端标签 */
    final Set<String> tags;
    /** 额外标签 */
    final Set<String> extraTags;
    /** 标签视图 */
    final Set<String> tagsView;
    /** 客户端类型 */
    final BrokerClientType type;
    /** 客户端地址 */
    final String address;

    public BrokerClientInfo(String name, Set<String> tags, Set<String> extraTags, BrokerClientType type, String address) {
        this.name = name;
        this.tags = tags;
        this.extraTags = extraTags;
        this.tagsView = Sets.union(tags, extraTags);
        this.type = type;
        this.address = address;
    }

    public void addExtraTag(String tag) {
        if (hasTag(tag)) {
            throw new IllegalArgumentException(String.format("tag '%s' is already exists", tag));
        }
        extraTags.add(tag);
    }

    public Set<String> getTags() {
        return Collections.unmodifiableSet(tags);
    }

    public Set<String> getExtraTags() {
        return Collections.unmodifiableSet(extraTags);
    }

    public boolean hasTag(String tag) {
        return tagsView.contains(tag);
    }

    public boolean hasAnyTags(String... tags) {
        
        for (String tag : tags) {
            if (this.tagsView.contains(tag)) {
                return true;
            }
        }
        return false;
    }

    public boolean hasAnyTags(Iterable<String> tags) {
        for (String tag : tags) {
            if (this.tagsView.contains(tag)) {
                return true;
            }
        }
        return false;
    }

    public boolean hasAllTags(String... tags) {
        for (String tag : tags) {
            if (!this.tagsView.contains(tag)) {
                return false;
            }
        }
        return true;
    }

    public boolean hasAllTags(Iterable<String> tags) {
        for (String tag : tags) {
            if (!this.tagsView.contains(tag)) {
                return false;
            }
        }
        return true;
    }

    public BrokerClientInfoMessage toMessage() {
        return new BrokerClientInfoMessage()
                .setName(name)
                .setTags(tags)
                .setExtraTags(extraTags)
                .setType(type)
                .setAddress(address);
    }
}
