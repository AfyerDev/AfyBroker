package net.afyer.afybroker.server.processor;

import com.alipay.remoting.AsyncContext;
import com.alipay.remoting.BizContext;
import com.alipay.remoting.rpc.exception.InvokeException;
import com.alipay.remoting.rpc.protocol.AsyncUserProcessor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import net.afyer.afybroker.core.message.RpcInvocationMessage;
import net.afyer.afybroker.core.util.AbstractInvokeCallback;
import net.afyer.afybroker.server.BrokerServer;
import net.afyer.afybroker.server.aware.BrokerServerAware;
import net.afyer.afybroker.server.proxy.BrokerClientItem;

/**
 * 服务器端RPC调用处理器
 * 
 * @author Nipuru
 * @since 2025/7/11 18:04
 */
@Slf4j
public class RpcInvocationBrokerProcessor extends AsyncUserProcessor<RpcInvocationMessage> implements BrokerServerAware {

    @Setter
    private BrokerServer brokerServer;

    @Override
    public void handleRequest(BizContext bizCtx, AsyncContext asyncCtx, RpcInvocationMessage request) {
        try {
            log.debug("Handling RPC invocation: {}.{}", request.getServiceInterface(), request.getMethodName());
            
            // 根据服务接口和标签选择合适的服务提供者
            BrokerClientItem serviceProvider = brokerServer.getServiceRegistry().selectServiceProvider(
                request.getServiceInterface(), 
                request.getServiceTags(), 
                brokerServer.getClientManager()
            );
            
            if (serviceProvider == null) {
                String errorMsg = String.format("No service provider found for: %s with tags: %s", 
                    request.getServiceInterface(), request.getServiceTags());
                log.warn(errorMsg);
                asyncCtx.sendException(new InvokeException(errorMsg));
                return;
            }
            
            log.debug("Selected service provider: {} for service: {}", 
                serviceProvider.getName(), request.getServiceInterface());
            
            // 转发RPC调用到服务提供者
            serviceProvider.invokeWithCallback(request, new AbstractInvokeCallback() {
                @Override
                public void onResponse(Object result) {
                    log.debug("RPC invocation completed: {}.{}", 
                        request.getServiceInterface(), request.getMethodName());
                    asyncCtx.sendResponse(result);
                }
                
                @Override
                public void onException(Throwable e) {
                    log.error("RPC invocation failed: {}.{}", 
                        request.getServiceInterface(), request.getMethodName(), e);
                    asyncCtx.sendException(e);
                }
            });
            
        } catch (Exception e) {
            log.error("Failed to handle RPC invocation: {}.{}", 
                request.getServiceInterface(), request.getMethodName(), e);
            asyncCtx.sendException(e);
        }
    }

    @Override
    public String interest() {
        return RpcInvocationMessage.class.getName();
    }
} 