package net.afyer.afybroker.server.processor;

import com.alipay.remoting.AsyncContext;
import com.alipay.remoting.BizContext;
import com.alipay.remoting.rpc.exception.InvokeException;
import com.alipay.remoting.rpc.protocol.AsyncUserProcessor;
import net.afyer.afybroker.core.message.RpcInvocationMessage;
import net.afyer.afybroker.core.util.AbstractInvokeCallback;
import net.afyer.afybroker.server.BrokerServer;
import net.afyer.afybroker.server.aware.BrokerServerAware;
import net.afyer.afybroker.server.proxy.BrokerClientItem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 服务器端RPC调用处理器
 * 
 * @author Nipuru
 * @since 2025/7/11 18:04
 */
public class RpcInvocationBrokerProcessor extends AsyncUserProcessor<RpcInvocationMessage> implements BrokerServerAware {

    private static final Logger LOGGER = LoggerFactory.getLogger(RpcInvocationBrokerProcessor.class);

    private BrokerServer brokerServer;

    public void setBrokerServer(BrokerServer brokerServer) {
        this.brokerServer = brokerServer;
    }

    @Override
    public void handleRequest(BizContext bizCtx, AsyncContext asyncCtx, RpcInvocationMessage request) {
        try {
            LOGGER.debug("Handling RPC invocation: {}.{}", request.getServiceInterface(), request.getMethodName());
            
            // 根据服务接口和标签选择合适的服务提供者
            BrokerClientItem serviceProvider = brokerServer.getServiceRegistry().selectServiceProvider(
                request.getServiceInterface(), 
                request.getServiceTags(), 
                brokerServer.getClientManager()
            );
            
            if (serviceProvider == null) {
                String errorMsg = String.format("No service provider found for: %s with tags: %s", 
                    request.getServiceInterface(), request.getServiceTags());
                LOGGER.warn(errorMsg);
                asyncCtx.sendException(new InvokeException(errorMsg));
                return;
            }

            LOGGER.debug("Selected service provider: {} for service: {}",
                serviceProvider.getName(), request.getServiceInterface());
            
            // 转发RPC调用到服务提供者
            serviceProvider.invokeWithCallback(request, new AbstractInvokeCallback() {
                @Override
                public void onResponse(Object result) {
                    LOGGER.debug("RPC invocation completed: {}.{}",
                        request.getServiceInterface(), request.getMethodName());
                    asyncCtx.sendResponse(result);
                }
                
                @Override
                public void onException(Throwable e) {
                    LOGGER.error("RPC invocation failed: {}.{}",
                        request.getServiceInterface(), request.getMethodName(), e);
                    asyncCtx.sendException(e);
                }
            });
            
        } catch (Exception e) {
            LOGGER.error("Failed to handle RPC invocation: {}.{}",
                request.getServiceInterface(), request.getMethodName(), e);
            asyncCtx.sendException(e);
        }
    }

    @Override
    public String interest() {
        return RpcInvocationMessage.class.getName();
    }
} 