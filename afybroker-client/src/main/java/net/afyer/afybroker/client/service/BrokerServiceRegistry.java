package net.afyer.afybroker.client.service;

import com.alipay.remoting.rpc.exception.InvokeException;
import net.afyer.afybroker.core.BrokerServiceDescriptor;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
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
        this.services = services;
    }


    public List<BrokerServiceDescriptor> getDescriptors() {
        List<BrokerServiceDescriptor> descriptors = new ArrayList<>(services.size());
        for (BrokerServiceEntry entry : services.values()) {
            descriptors.add(entry.getDescriptor());
        }
        return descriptors;
    }

    /**
     * 调用本地服务
     */
    public Object invoke(String serviceInterface, String methodName,
                         String[] parameterTypeNames, Object[] parameters)
            throws Exception {
        BrokerServiceEntry entry = services.get(serviceInterface);
        if (entry == null) {
            throw new InvokeException("Service not found: " + serviceInterface);
        }

        // 从缓存中获取Method
        Method method = entry.getMethod(methodName, parameterTypeNames);
        if (method == null) {
            throw new InvokeException("Method not found: " + methodName + 
                    " with parameters: " + Arrays.toString(parameterTypeNames));
        }
        return method.invoke(entry.getServiceImpl(), parameters);
    }



} 