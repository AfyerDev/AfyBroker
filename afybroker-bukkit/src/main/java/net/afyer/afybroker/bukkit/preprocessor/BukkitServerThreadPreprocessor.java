package net.afyer.afybroker.bukkit.preprocessor;

import lombok.extern.slf4j.Slf4j;
import net.afyer.afybroker.client.preprocessor.BrokerInvocationContext;
import net.afyer.afybroker.client.preprocessor.BrokerPreprocessor;
import net.afyer.afybroker.client.preprocessor.PreprocessorException;
import org.bukkit.Bukkit;

/**
 * Bukkit主线程安全检查预处理器
 * 防止在服务器主线程中发起远程调用，避免阻塞主线程
 * 
 * @author Nipuru
 * @since 2025/09/10 14:41
 */
public class BukkitServerThreadPreprocessor implements BrokerPreprocessor {
    
    private final boolean enabled;
    
    public BukkitServerThreadPreprocessor(boolean enabled) {
        this.enabled = enabled;
    }
    
    @Override
    public void preprocess(BrokerInvocationContext context) throws PreprocessorException {
        if (!enabled) {
            return;
        }
        
        Thread currentThread = context.getThread();

        if (Bukkit.isPrimaryThread()) {
            String message = String.format(
                "Remote call blocked: Cannot make remote calls from Server main thread. " +
                "Thread: %s (ID: %d), Method: %s, Request: %s",
                currentThread.getName(),
                currentThread.getId(),
                context.getMethodName(),
                context.getRequest().getClass().getSimpleName()
            );

            throw new PreprocessorException(message);
        }
    }
}
