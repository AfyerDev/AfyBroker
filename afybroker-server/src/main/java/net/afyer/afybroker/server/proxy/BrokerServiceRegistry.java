package net.afyer.afybroker.server.proxy;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/** 维护服务与唯一提供者的映射，整批注册信息校验通过后才发布。 */
public class BrokerServiceRegistry {
    private final Map<String, BrokerClientItem> providers = new HashMap<>();

    public synchronized void registerClientServices(BrokerClientItem client, List<String> serviceKeys) {
        Set<String> keys = keys(serviceKeys);
        for (String key : keys) {
            BrokerClientItem owner = providers.get(key);
            if (owner != null && owner != client) {
                throw new IllegalArgumentException("Duplicate service " + key + "; owner=" + owner.getName());
            }
        }
        for (String key : keys) providers.put(key, client);
    }

    static Set<String> keys(List<String> serviceKeys) {
        if (serviceKeys == null) throw new IllegalArgumentException("Service list must not be null");
        Set<String> keys = new HashSet<>();
        for (String key : serviceKeys) {
            if (key == null || key.trim().isEmpty()) throw new IllegalArgumentException("Service key must not be blank");
            if (!keys.add(key)) throw new IllegalArgumentException("Duplicate service in registration: " + key);
        }
        return keys;
    }

    public synchronized void unregisterClientServices(BrokerClientItem client) {
        providers.values().removeIf(owner -> owner == client);
    }

    public synchronized BrokerClientItem getServiceProvider(String serviceKey) {
        return providers.get(serviceKey);
    }

    public synchronized Set<String> getAllServiceKeys() {
        return new HashSet<>(providers.keySet());
    }
}
