package net.afyer.afybroker.core.message;

import java.io.Serializable;
import java.util.List;

/**
 * @author Nipuru
 * @since 2025/10/04 16:22
 */
public class CloseBrokerClientMessage implements Serializable {
    /** 指定名称 */
    public List<String> names;

    /** 指定类型 */
    public List<String> types;

    /** 指定标签 */
    public List<String> tags;

    public List<String> getNames() {
        return names;
    }

    public CloseBrokerClientMessage setNames(List<String> names) {
        this.names = names;
        return this;
    }

    public List<String> getTypes() {
        return types;
    }

    public CloseBrokerClientMessage setTypes(List<String> types) {
        this.types = types;
        return this;
    }

    public List<String> getTags() {
        return tags;
    }

    public CloseBrokerClientMessage setTags(List<String> tags) {
        this.tags = tags;
        return this;
    }
}
