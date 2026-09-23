package net.afyer.afybroker.core.message;

import java.io.Serializable;

/**
 * RPC调用消息
 *
 * @author Nipuru
 * @since 2025/7/11 18:04
 */
public class RpcInvocationMessage implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 服务标识：接口名或接口名#标签
     */
    private String serviceKey;

    /**
     * 方法名
     */
    private String methodName;

    /**
     * 参数类型
     */
    private String[] parameterTypes;

    /**
     * 参数值
     */
    private byte[] parameters;

    public String getServiceKey() {
        return serviceKey;
    }

    public RpcInvocationMessage setServiceKey(String serviceKey) {
        this.serviceKey = serviceKey;
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

    @Override
    public String toString() {
        return "RpcInvocationMessage{" +
                "serviceKey='" + serviceKey + '\'' +
                ", methodName='" + methodName + '\'' +
                ", parameterTypes=" + java.util.Arrays.toString(parameterTypes) +
                ", parameters=" + java.util.Arrays.toString(parameters) +
                '}';
    }
}
