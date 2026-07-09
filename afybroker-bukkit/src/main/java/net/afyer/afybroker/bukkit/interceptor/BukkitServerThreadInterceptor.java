package net.afyer.afybroker.bukkit.interceptor;

import com.alipay.remoting.exception.RemotingException;
import net.afyer.afybroker.core.interceptor.Interceptor;
import net.afyer.afybroker.core.interceptor.Invocation;
import net.afyer.afybroker.core.interceptor.InvocationContext;
import net.afyer.afybroker.core.message.RpcInvocationMessage;
import org.bukkit.Bukkit;

import java.util.Arrays;

public class BukkitServerThreadInterceptor implements Interceptor {

    private final boolean enabled;

    public BukkitServerThreadInterceptor(boolean enabled) {
        this.enabled = enabled;
    }

    @Override
    public Object intercept(Invocation invocation) throws Throwable {
        if (!enabled) {
            return invocation.proceed();
        }
        if (!Bukkit.isPrimaryThread()) {
            return invocation.proceed();
        }

        InvocationContext context = invocation.getContext();
        throw new RemotingException(buildMessage(context));
    }

    private String buildMessage(InvocationContext context) {
        if (context.isServiceInvocation()) {
            RpcInvocationMessage request = context.getRpcRequest();
            return String.format(
                    "Remote RPC blocked: cannot invoke remote service from Bukkit main thread. " +
                            "Thread: %s (ID: %d), Mode: %s, Service: %s, Method: %s, ParameterTypes: %s, Tags: %s",
                    context.getThread().getName(),
                    context.getThread().getId(),
                    context.getType(),
                    request.getServiceInterface(),
                    request.getMethodName(),
                    Arrays.toString(request.getParameterTypes()),
                    request.getServiceTags()
            );
        } else {
            return String.format(
                    "Remote message blocked: cannot send remote message from Bukkit main thread. " +
                            "Thread: %s (ID: %d), Mode: %s, Request: %s",
                    context.getThread().getName(),
                    context.getThread().getId(),
                    context.getType(),
                    context.getRequest().getClass().getName()
            );
        }
    }
}
