package net.afyer.afybroker.client.preprocessor;

/**
 * Broker调用上下文
 * 
 * @author Nipuru
 * @since 2025/09/10 11:53
 */
public class BrokerInvocationContext {

    /** 服务接口名称 */
    private final Object request;
    
    /** 方法名称 */
    private final String methodName;

    /** 超时时间 */
    private final int timeoutMillis;
    
    /** 当前线程 */
    private final Thread thread;

    public BrokerInvocationContext(Object request, String methodName, int timeoutMillis, Thread thread) {
        this.request = request;
        this.methodName = methodName;
        this.timeoutMillis = timeoutMillis;
        this.thread = thread;
    }

    public Object getRequest() {
        return request;
    }

    public String getMethodName() {
        return methodName;
    }

    public int getTimeoutMillis() {
        return timeoutMillis;
    }

    public Thread getThread() {
        return thread;
    }
}
