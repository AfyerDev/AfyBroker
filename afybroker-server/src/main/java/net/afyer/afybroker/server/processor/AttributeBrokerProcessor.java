package net.afyer.afybroker.server.processor;

import com.alipay.remoting.BizContext;
import com.alipay.remoting.rpc.protocol.SyncUserProcessor;
import net.afyer.afybroker.core.Attributable;
import net.afyer.afybroker.core.message.AttributeMessage;
import net.afyer.afybroker.server.BrokerServer;
import net.afyer.afybroker.server.aware.BrokerServerAware;
import net.afyer.afybroker.server.proxy.BrokerPlayer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

/**
 * 属性操作处理器
 * 统一处理 SET/GET/REMOVE/HAS 操作，支持 SERVER/PLAYER 两种作用域
 *
 * @author Conan-Wen
 * @since 2026/3/22
 */
public class AttributeBrokerProcessor extends SyncUserProcessor<AttributeMessage> implements BrokerServerAware {

    private static final Logger LOGGER = LoggerFactory.getLogger(AttributeBrokerProcessor.class);

    private BrokerServer brokerServer;

    @Override
    public void setBrokerServer(BrokerServer brokerServer) {
        this.brokerServer = brokerServer;
    }

    @Override
    public Object handleRequest(BizContext bizCtx, AttributeMessage request) throws Exception {
        Attributable target;
        if (request.getScope() == AttributeMessage.SCOPE_SERVER) {
            target = brokerServer;
        } else {
            BrokerPlayer player = brokerServer.getPlayer(request.getUniqueId());
            if (player == null) {
                LOGGER.warn("Player not found for attribute operation: {}", request.getUniqueId());
                return null;
            }
            target = player;
        }
        Map<String, byte[]> rawAttributes = target.getAttributeContainer().getRawAttributes();

        switch (request.getAction()) {
            case AttributeMessage.ACTION_SET:
                rawAttributes.put(request.getKey(), request.getValue());
                return null;
            case AttributeMessage.ACTION_GET:
                return rawAttributes.get(request.getKey());
            case AttributeMessage.ACTION_REMOVE:
                return rawAttributes.remove(request.getKey());
            case AttributeMessage.ACTION_HAS:
                return rawAttributes.containsKey(request.getKey());
            default:
                LOGGER.warn("Unknown attribute action: {}", request.getAction());
                return null;
        }
    }

    @Override
    public String interest() {
        return AttributeMessage.class.getName();
    }
}