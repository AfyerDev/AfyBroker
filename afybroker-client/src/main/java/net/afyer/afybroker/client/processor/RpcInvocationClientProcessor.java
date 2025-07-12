package net.afyer.afybroker.client.processor;

import com.alipay.remoting.AsyncContext;
import com.alipay.remoting.BizContext;
import com.alipay.remoting.rpc.protocol.AsyncUserProcessor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import net.afyer.afybroker.client.BrokerClient;
import net.afyer.afybroker.client.aware.BrokerClientAware;
import net.afyer.afybroker.core.message.RpcInvocationMessage;
import net.afyer.afybroker.core.util.HessianSerializer;

/**
 * 客户端RPC调用处理器
 * 
 * @author Nipuru
 * @since 2025/7/11 18:04
 */
@Slf4j
public class RpcInvocationClientProcessor extends AsyncUserProcessor<RpcInvocationMessage> implements BrokerClientAware {

    @Setter
    private BrokerClient brokerClient;

    @Override
    public void handleRequest(BizContext bizCtx, AsyncContext asyncCtx, RpcInvocationMessage request) {
        try {
            log.debug("Handling RPC invocation: {}.{}", request.getServiceInterface(), request.getMethodName());
            
            // 调用本地服务
            Object result = brokerClient.getServiceRegistry().invoke(
                request.getServiceInterface(),
                request.getMethodName(),
                request.getParameterTypes(),
                    request.getParameters()
            );

            byte[] response = null;
            if (result != null) {
                response = HessianSerializer.serialize(result);
            }
            // 返回结果
            asyncCtx.sendResponse(response);
        } catch (Exception e) {
            log.error("RPC invocation failed: {}.{}", request.getServiceInterface(), request.getMethodName(), e);
            asyncCtx.sendException(e);
        }
    }

    @Override
    public String interest() {
        return RpcInvocationMessage.class.getName();
    }
} 