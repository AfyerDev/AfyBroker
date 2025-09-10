package net.afyer.afybroker.client.preprocessor;

/**
 * 预处理函数接口
 * 在远程调用前执行，用于进行安全检查等操作
 * 
 * @author Nipuru
 * @since  2025/09/10 11:53
 */
@FunctionalInterface
public interface BrokerPreprocessor {
    
    /**
     * 预处理函数
     * 
     * @param context 调用上下文，包含服务接口、方法名、参数等信息
     * @throws PreprocessorException 如果预处理失败，抛出此异常
     */
    void preprocess(BrokerInvocationContext context) throws PreprocessorException;
}
