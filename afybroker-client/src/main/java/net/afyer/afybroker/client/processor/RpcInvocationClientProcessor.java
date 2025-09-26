package net.afyer.afybroker.client.processor;

import com.alipay.remoting.AsyncContext;
import com.alipay.remoting.BizContext;
import com.alipay.remoting.config.ConfigManager;
import com.alipay.remoting.rpc.protocol.AsyncUserProcessor;
import com.alipay.remoting.serialization.Serializer;
import com.alipay.remoting.serialization.SerializerManager;
import net.afyer.afybroker.client.BrokerClient;
import net.afyer.afybroker.client.aware.BrokerClientAware;
import net.afyer.afybroker.core.message.RpcInvocationMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 客户端RPC调用处理器
 * 
 * @author Nipuru
 * @since 2025/7/11 18:04
 */
public class RpcInvocationClientProcessor extends AsyncUserProcessor<RpcInvocationMessage> implements BrokerClientAware {

    private static final Logger LOGGER = LoggerFactory.getLogger(RpcInvocationClientProcessor.class);

    private BrokerClient brokerClient;

    public void setBrokerClient(BrokerClient brokerClient) {
        this.brokerClient = brokerClient;
    }

    @Override
    public void handleRequest(BizContext bizCtx, AsyncContext asyncCtx, RpcInvocationMessage request) {
        try {
            LOGGER.debug("Handling RPC invocation: {}.{}", request.getServiceInterface(), request.getMethodName());

            Serializer serializer = SerializerManager.getSerializer(ConfigManager.serializer());
            // 调用本地服务
            Object result = brokerClient.getServiceRegistry().invoke(
                request.getServiceInterface(),
                request.getMethodName(),
                request.getParameterTypes(),
                serializer.deserialize(request.getParameters(), Object[].class.getName())
            );

            byte[] response = null;
            if (result != null) {
                response = serializer.serialize(result);
            }
            // 返回结果
            asyncCtx.sendResponse(response);
        } catch (Exception e) {
            LOGGER.error("RPC invocation failed: {}.{}", request.getServiceInterface(), request.getMethodName(), e);
            asyncCtx.sendException(e);
        }
    }

    @Override
    public String interest() {
        return RpcInvocationMessage.class.getName();
    }
} 