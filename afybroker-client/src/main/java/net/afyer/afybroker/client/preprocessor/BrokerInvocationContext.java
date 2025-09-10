package net.afyer.afybroker.client.preprocessor;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.lang.reflect.Method;
import java.util.Set;

/**
 * Broker调用上下文
 * 
 * @author Nipuru
 * @since 2025/09/10 11:53
 */
@Getter
@AllArgsConstructor
public class BrokerInvocationContext {

    /** 服务接口名称 */
    private final Object request;
    
    /** 方法名称 */
    private final String methodName;

    /** 超时时间 */
    private final int timeoutMillis;
    
    /** 当前线程 */
    private final Thread thread;
}
