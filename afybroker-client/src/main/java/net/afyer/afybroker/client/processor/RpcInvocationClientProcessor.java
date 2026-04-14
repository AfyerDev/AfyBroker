package net.afyer.afybroker.client.processor;

import com.alipay.remoting.AsyncContext;
import com.alipay.remoting.BizContext;
import com.alipay.remoting.config.ConfigManager;
import com.alipay.remoting.rpc.protocol.AsyncUserProcessor;
import com.alipay.remoting.serialization.Serializer;
import com.alipay.remoting.serialization.SerializerManager;
import net.afyer.afybroker.client.BrokerClient;
import net.afyer.afybroker.client.aware.BrokerClientAware;
import net.afyer.afybroker.core.observability.ObservabilitySupport;
import net.afyer.afybroker.core.observability.RpcObservation;
import net.afyer.afybroker.core.observability.RpcPhase;
import net.afyer.afybroker.core.message.RpcInvocationMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RpcInvocationClientProcessor extends AsyncUserProcessor<RpcInvocationMessage> implements BrokerClientAware {

    private static final Logger LOGGER = LoggerFactory.getLogger(RpcInvocationClientProcessor.class);

    private BrokerClient brokerClient;

    @Override
    public void setBrokerClient(BrokerClient brokerClient) {
        this.brokerClient = brokerClient;
    }

    @Override
    public void handleRequest(BizContext bizCtx, AsyncContext asyncCtx, RpcInvocationMessage request) {
        long startNanos = System.nanoTime();
        try {
            LOGGER.debug("Handling RPC invocation: {}.{}", request.getServiceInterface(), request.getMethodName());

            Serializer serializer = SerializerManager.getSerializer(ConfigManager.serializer());
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
            record(request, startNanos, true);
            asyncCtx.sendResponse(response);
        } catch (Exception e) {
            record(request, startNanos, false);
            LOGGER.error("RPC invocation failed: {}.{}", request.getServiceInterface(), request.getMethodName(), e);
            asyncCtx.sendException(e);
        }
    }

    private void record(RpcInvocationMessage request, long startNanos, boolean success) {
        brokerClient.getObservability().onRpc(new RpcObservation(
                RpcPhase.SERVICE,
                ObservabilitySupport.requestType(request),
                ObservabilitySupport.serviceInterface(request),
                ObservabilitySupport.methodName(request),
                success,
                System.nanoTime() - startNanos
        ));
    }

    @Override
    public String interest() {
        return RpcInvocationMessage.class.getName();
    }
}
