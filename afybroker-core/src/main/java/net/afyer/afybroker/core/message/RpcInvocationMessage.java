package net.afyer.afybroker.core.message;

import java.io.Serializable;
import java.util.Set;

/**
 * RPC调用消息
 * 
 * @author Nipuru
 * @since 2025/7/11 18:04
 */
public class RpcInvocationMessage implements Serializable {
    private static final long serialVersionUID = 1L;
    
    /** 服务接口名 */
    private String serviceInterface;
    
    /** 方法名 */
    private String methodName;
    
    /** 参数类型 */
    private String[] parameterTypes;
    
    /** 参数值 */
    private byte[] parameters;
    
    /** 服务标签，用于服务选择 */
    private Set<String> serviceTags;

    public String getServiceInterface() {
        return serviceInterface;
    }

    public RpcInvocationMessage setServiceInterface(String serviceInterface) {
        this.serviceInterface = serviceInterface;
        return this;
    }

    public String getMethodName() {
        return methodName;
    }

    public RpcInvocationMessage setMethodName(String methodName) {
        this.methodName = methodName;
        return this;
    }

    public String[] getParameterTypes() {
        return parameterTypes;
    }

    public RpcInvocationMessage setParameterTypes(String[] parameterTypes) {
        this.parameterTypes = parameterTypes;
        return this;
    }

    public byte[] getParameters() {
        return parameters;
    }

    public RpcInvocationMessage setParameters(byte[] parameters) {
        this.parameters = parameters;
        return this;
    }

    public Set<String> getServiceTags() {
        return serviceTags;
    }

    public RpcInvocationMessage setServiceTags(Set<String> serviceTags) {
        this.serviceTags = serviceTags;
        return this;
    }

    @Override
    public String toString() {
        return "RpcInvocationMessage{" +
                "serviceInterface='" + serviceInterface + '\'' +
                ", methodName='" + methodName + '\'' +
                ", parameterTypes=" + java.util.Arrays.toString(parameterTypes) +
                ", parameters=" + java.util.Arrays.toString(parameters) +
                ", serviceTags=" + serviceTags +
                '}';
    }
} 