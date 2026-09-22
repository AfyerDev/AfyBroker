package net.afyer.afybroker.core;

import java.io.Serializable;

/**
 * 服务描述符
 *
 * @author Nipuru
 * @since 2025/7/11 17:04
 */
public class BrokerServiceDescriptor implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 服务接口名
     */
    private String serviceInterface;

    /**
     * 服务标签
     */
    private String tag;

    public String getServiceInterface() {
        return serviceInterface;
    }

    public BrokerServiceDescriptor setServiceInterface(String serviceInterface) {
        this.serviceInterface = serviceInterface;
        return this;
    }

    public String getTag() {
        return tag;
    }

    public BrokerServiceDescriptor setTag(String tag) {
        this.tag = tag;
        return this;
    }

    @Override
    public String toString() {
        return "BrokerServiceDescriptor{" +
                "serviceInterface='" + serviceInterface + '\'' +
                ", tag=" + tag +
                '}';
    }
} 