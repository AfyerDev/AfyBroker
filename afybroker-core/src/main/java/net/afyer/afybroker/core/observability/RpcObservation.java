package net.afyer.afybroker.core.observability;

import net.afyer.afybroker.core.util.BoltUtils;

public class RpcObservation {
    private final RpcPhase phase;
    private final String serviceKey;
    private final String methodName;
    private final boolean success;
    private final long durationNanos;

    public RpcObservation(RpcPhase phase, String serviceKey, String methodName, boolean success, long durationNanos) {
        this.phase = phase;
        this.serviceKey = serviceKey;
        this.methodName = methodName;
        this.success = success;
        this.durationNanos = durationNanos;
    }

    public RpcPhase getPhase() {
        return phase;
    }


    public String getServiceKey() {
        return serviceKey;
    }

    public String getServiceInterface() {
        return BoltUtils.serviceInterface(serviceKey);
    }

    public String getServiceTag() {
        return BoltUtils.serviceTag(serviceKey);
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
