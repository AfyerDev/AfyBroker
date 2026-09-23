package net.afyer.afybroker.client.service;

import com.alipay.remoting.rpc.exception.InvokeException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 服务注册表
 *
 * @author Nipuru
 * @since 2025/7/11 17:06
 */
public class BrokerServiceRegistry {

    private final Map<String, BrokerServiceEntry> services;

    public BrokerServiceRegistry(Map<String, BrokerServiceEntry> services) {
        this.services = new HashMap<>(services);
    }


    public List<String> getServiceKeys() {
        return new ArrayList<>(services.keySet());
    }

    /** 调用本地服务  */
    public Object invoke(String serviceKey, String methodName,
                         String[] parameterTypeNames, Object[] parameters)
            throws Throwable {
        BrokerServiceEntry entry = services.get(serviceKey);
        if (entry == null) {
            throw new InvokeException("Service not found: " + serviceKey);
        }

        return entry.invoke(methodName, parameterTypeNames, parameters);
    }
}
