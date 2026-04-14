package net.afyer.afybroker.core.observability;

public class RpcObservation {
    private final RpcPhase phase;
    private final String requestType;
    private final String serviceInterface;
    private final String methodName;
    private final boolean success;
    private final long durationNanos;

    public RpcObservation(RpcPhase phase,
                          String requestType, String serviceInterface, String methodName,
                          boolean success, long durationNanos) {
        this.phase = phase;
        this.requestType = requestType;
        this.serviceInterface = serviceInterface;
        this.methodName = methodName;
        this.success = success;
        this.durationNanos = durationNanos;
    }

    public RpcPhase getPhase() {
        return phase;
    }

    public String getRequestType() {
        return requestType;
    }

    public String getServiceInterface() {
        return serviceInterface;
    }

    public String getMethodName() {
        return methodName;
    }

    public boolean isSuccess() {
        return success;
    }

    public long getDurationNanos() {
        return durationNanos;
    }

}
