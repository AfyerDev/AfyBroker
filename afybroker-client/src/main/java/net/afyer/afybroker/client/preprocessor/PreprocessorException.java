package net.afyer.afybroker.client.preprocessor;

import com.alipay.remoting.exception.RemotingException;

/**
 * 预处理异常
 * 
 * @author Nipuru
 * @since  2025/09/10 11:53
 */
public class PreprocessorException extends RemotingException {
    
    public PreprocessorException(String message) {
        super(message);
    }
    
    public PreprocessorException(String message, Throwable cause) {
        super(message, cause);
    }
}
