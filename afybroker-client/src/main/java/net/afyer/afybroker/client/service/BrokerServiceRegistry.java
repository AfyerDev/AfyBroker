package net.afyer.afybroker.client.service;

import com.alipay.remoting.rpc.exception.InvokeException;
import com.alipay.remoting.serialization.Serializer;
import com.alipay.remoting.serialization.SerializerManager;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
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
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE)
public class BrokerServiceRegistry {

    final Map<String, BrokerServiceEntry> services;

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
                         String[] parameterTypeNames, byte[] parametersData)
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

        Serializer serializer = SerializerManager.getSerializer(SerializerManager.Hessian2);
        Object[] parameters = serializer.deserialize(parametersData, Object[].class.getName());
        return method.invoke(entry.getServiceImpl(), parameters);
    }



} 