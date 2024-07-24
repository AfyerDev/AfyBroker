package net.afyer.afybroker.core.util;

import com.alipay.remoting.InvokeCallback;

import java.util.concurrent.Executor;

/**
 * @author Nipuru
 * @since 2022/12/18 17:32
 */
public abstract class AbstractInvokeCallback implements InvokeCallback {

    @Override
    public void onResponse(Object result) { }

    @Override
    public void onException(Throwable e) { }

    @Override
    public Executor getExecutor() {
        return null;
    }

    @SuppressWarnings("unchecked")
    public static <T> T cast(Object object) {
        return (T) object;
    }

}
