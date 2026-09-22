package net.afyer.afybroker.core;

import java.util.Objects;

/** 服务的唯一标识，由接口和可选标签组成，包含无标签的服务。 */
public final class BrokerServiceKey {
    private final String serviceInterface;
    private final String tag;

    public BrokerServiceKey(String serviceInterface, String tag) {
        if (serviceInterface == null || serviceInterface.trim().isEmpty()) {
            throw new IllegalArgumentException("Service interface must not be blank");
        }
        if (tag != null && tag.trim().isEmpty()) {
            throw new IllegalArgumentException("Service tag must not be blank; use null for an untagged service");
        }
        this.serviceInterface = serviceInterface;
        this.tag = tag;
    }

    public String getServiceInterface() {
        return serviceInterface;
    }

    public String getTag() {
        return tag;
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) return true;
        if (!(other instanceof BrokerServiceKey)) return false;
        BrokerServiceKey key = (BrokerServiceKey) other;
        return serviceInterface.equals(key.serviceInterface) && Objects.equals(tag, key.tag);
    }

    @Override
    public int hashCode() {
        return Objects.hash(serviceInterface, tag);
    }

    @Override
    public String toString() {
        return serviceInterface + " [tag=" + (tag == null ? "<untagged>" : tag) + "]";
    }
}
