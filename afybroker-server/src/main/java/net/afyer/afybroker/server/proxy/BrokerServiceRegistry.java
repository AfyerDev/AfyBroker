package net.afyer.afybroker.server.proxy;

import net.afyer.afybroker.core.BrokerServiceDescriptor;
import net.afyer.afybroker.core.BrokerServiceKey;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/** 维护服务与唯一提供者的映射，整批注册信息校验通过后才发布。 */
public class BrokerServiceRegistry {
    private final Map<BrokerServiceKey, BrokerClientItem> providers = new HashMap<>();

    public synchronized void registerClientServices(BrokerClientItem client, List<BrokerServiceDescriptor> services) {
        Set<BrokerServiceKey> keys = keys(services);
        for (BrokerServiceKey key : keys) {
            BrokerClientItem owner = providers.get(key);
            if (owner != null && owner != client) {
                throw new IllegalArgumentException("Duplicate service " + key + "; owner=" + owner.getName());
            }
        }
        for (BrokerServiceKey key : keys) providers.put(key, client);
    }

    static Set<BrokerServiceKey> keys(List<BrokerServiceDescriptor> services) {
        if (services == null) throw new IllegalArgumentException("Service list must not be null");
        Set<BrokerServiceKey> keys = new HashSet<>();
        for (BrokerServiceDescriptor service : services) {
            if (service == null) throw new IllegalArgumentException("Service descriptor must not be null");
            BrokerServiceKey key = new BrokerServiceKey(service.getServiceInterface(), service.getTag());
            if (!keys.add(key)) throw new IllegalArgumentException("Duplicate service in registration: " + key);
        }
        return keys;
    }

    public synchronized void unregisterClientServices(BrokerClientItem client) {
        providers.values().removeIf(owner -> owner == client);
    }

    public synchronized BrokerClientItem getServiceProvider(String serviceInterface, String tag) {
        return providers.get(new BrokerServiceKey(serviceInterface, tag));
    }

    public synchronized Set<String> getAllServiceInterfaces() {
        Set<String> interfaces = new HashSet<>();
        for (BrokerServiceKey key : providers.keySet()) interfaces.add(key.getServiceInterface());
        return interfaces;
    }
}
