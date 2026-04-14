package net.afyer.afybroker.core.observability;

import net.afyer.afybroker.core.message.RpcInvocationMessage;

public final class ObservabilitySupport {

    private ObservabilitySupport() {
    }

    public static String requestType(Object request) {
        return request == null ? "unknown" : request.getClass().getSimpleName();
    }

    public static String serviceInterface(Object request) {
        if (request instanceof RpcInvocationMessage) {
            return labelValue(((RpcInvocationMessage) request).getServiceInterface());
        }
        return "none";
    }

    public static String methodName(Object request) {
        if (request instanceof RpcInvocationMessage) {
            return labelValue(((RpcInvocationMessage) request).getMethodName());
        }
        return "none";
    }

    public static String labelValue(String value) {
        if (value == null || value.trim().isEmpty()) {
            return "unknown";
        }
        return value.trim();
    }
}
