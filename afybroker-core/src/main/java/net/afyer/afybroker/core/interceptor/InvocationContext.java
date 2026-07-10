package net.afyer.afybroker.core.interceptor;

import net.afyer.afybroker.core.message.RpcInvocationMessage;

import java.util.HashMap;
import java.util.Map;

public class InvocationContext {

    private Object request;
    private Object response;
    private Throwable throwable;
    private Object callback;
    private int timeoutMillis;
    private String address;
    private final InvocationType type;
    private final Thread thread;
    private final Map<String, Object> attributes = new HashMap<>();

    public InvocationContext(Object request, InvocationType type, String address, int timeoutMillis) {
        setRequest(request);
        this.type = type;
        this.timeoutMillis = timeoutMillis;
        this.thread = Thread.currentThread();
        this.address = address;
    }

    public Object getRequest() {
        return request;
    }

    public InvocationContext setRequest(Object request) {
        this.request = request;
        return this;
    }

    public Object getResponse() {
        return response;
    }

    public InvocationContext setResponse(Object response) {
        this.response = response;
        return this;
    }

    public Throwable getThrowable() {
        return throwable;
    }

    public InvocationContext setThrowable(Throwable throwable) {
        this.throwable = throwable;
        return this;
    }

    public Object getCallback() {
        return callback;
    }

    public InvocationContext setCallback(Object callback) {
        this.callback = callback;
        return this;
    }

    public InvocationType getType() {
        return type;
    }

    public int getTimeoutMillis() {
        return timeoutMillis;
    }

    public InvocationContext setTimeoutMillis(int timeoutMillis) {
        this.timeoutMillis = timeoutMillis;
        return this;
    }

    public String getAddress() {
        return address;
    }

    public InvocationContext setAddress(String address) {
        this.address = address;
        return this;
    }

    public boolean isServiceInvocation() {
        return request instanceof RpcInvocationMessage;
    }

    public RpcInvocationMessage getRpcRequest() {
        if (request instanceof RpcInvocationMessage) {
            return (RpcInvocationMessage) request;
        }
        throw new IllegalStateException("Invocation request is not a RpcInvocationMessage: " +
                (request == null ? "null" : request.getClass().getName()));
    }

    public Thread getThread() {
        return thread;
    }

    public Map<String, Object> getAttributes() {
        return attributes;
    }

    @SuppressWarnings("unchecked")
    public <T> T getAttribute(String key) {
        return (T) attributes.get(key);
    }

    public <T> InvocationContext setAttribute(String key, T value) {
        attributes.put(key, value);
        return this;
    }

}
