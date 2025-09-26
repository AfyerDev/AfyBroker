package net.afyer.afybroker.server.proxy;

import net.afyer.afybroker.core.BrokerServiceDescriptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

/**
 * 服务注册表 - 服务器端
 * 
 * @author Nipuru
 * @since 2025/7/11 17:07
 */
public class BrokerServiceRegistry {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(BrokerServiceRegistry.class);
    
    /** 服务接口 -> 服务提供者列表 */
    private final Map<String, List<ServiceProvider>> serviceProviders = new ConcurrentHashMap<>();
    
    /** 客户端名称 -> 服务列表 */
    private final Map<String, Set<String>> clientServices = new ConcurrentHashMap<>();
    
    /**
     * 注册客户端服务
     */
    public void registerClientServices(BrokerClientItem client, List<BrokerServiceDescriptor> services) {
        // 先清理该客户端之前注册的服务
        unregisterClientServices(client);
        
        Set<String> registeredServices = new HashSet<>();
        
        for (BrokerServiceDescriptor service : services) {
            String serviceInterface = service.getServiceInterface();
            
            ServiceProvider provider = new ServiceProvider(client, service.getTags());
            
            serviceProviders.computeIfAbsent(serviceInterface, k -> new ArrayList<>()).add(provider);
            registeredServices.add(serviceInterface);

            LOGGER.info("Service registered: {} -> {} with tags: {}", serviceInterface, client.getName(), service.getTags());
        }
        
        clientServices.put(client.getName(), registeredServices);
    }
    
    /**
     * 取消注册客户端服务
     */
    public void unregisterClientServices(BrokerClientItem client) {
        Set<String> services = clientServices.remove(client.getName());
        if (services != null) {
            for (String serviceInterface : services) {
                List<ServiceProvider> providers = serviceProviders.get(serviceInterface);
                if (providers != null) {
                    providers.removeIf(provider -> provider.client.getName().equals(client.getName()));
                    if (providers.isEmpty()) {
                        serviceProviders.remove(serviceInterface);
                    }
                }
            }
            LOGGER.info("Unregistered {} services for client: {}", services.size(), client.getName());
        }
    }
    
    /**
     * 获取服务提供者
     */
    public List<BrokerClientItem> getServiceProviders(String serviceInterface, Set<String> tags, 
                                                      BrokerClientManager clientManager) {
        List<ServiceProvider> providers = serviceProviders.get(serviceInterface);
        if (providers == null || providers.isEmpty()) {
            return Collections.emptyList();
        }
        
        return providers.stream()
            .filter(provider -> matchesTags(provider.getTags(), tags))
            .map(provider -> provider.client)
            .filter(Objects::nonNull)
            .collect(Collectors.toList());
    }
    
    /**
     * 选择服务提供者（负载均衡）
     */
    public BrokerClientItem selectServiceProvider(String serviceInterface, Set<String> tags, 
                                                  BrokerClientManager clientManager) {
        List<BrokerClientItem> providers = getServiceProviders(serviceInterface, tags, clientManager);
        if (providers.isEmpty()) {
            return null;
        }
        
        // 简单的随机负载均衡
        return providers.get(ThreadLocalRandom.current().nextInt(providers.size()));
    }
    
    /**
     * 检查标签是否匹配
     */
    private boolean matchesTags(Set<String> providerTags, Set<String> requestTags) {
        if (requestTags == null || requestTags.isEmpty()) {
            return true; // 没有标签要求，匹配所有
        }
        
        if (providerTags == null || providerTags.isEmpty()) {
            return false; // 提供者没有标签，但请求有标签要求
        }
        
        // 提供者必须包含所有请求的标签
        return providerTags.containsAll(requestTags);
    }
    
    /**
     * 获取所有已注册的服务接口
     */
    public Set<String> getAllServiceInterfaces() {
        return new HashSet<>(serviceProviders.keySet());
    }
    
    private static class ServiceProvider {
        private final BrokerClientItem client;
        private final Set<String> tags;

        public ServiceProvider(BrokerClientItem client, Set<String> tags) {
            this.client = client;
            this.tags = tags;
        }

        public BrokerClientItem getClient() {
            return client;
        }

        public Set<String> getTags() {
            return tags;
        }
    }
} 