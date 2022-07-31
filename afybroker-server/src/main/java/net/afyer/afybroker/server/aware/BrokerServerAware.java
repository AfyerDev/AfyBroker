package net.afyer.afybroker.server.aware;

import net.afyer.afybroker.server.BrokerServer;

/**
 * BrokerServerAware
 * <p>
 *     设置 BrokerServer 实例
 *     实现了该接口的对象通过{@link BrokerServer#aware(Object)} 即可设置实例
 * </p>
 * @author Nipuru
 * @since 2022/7/31 19:53
 */
public interface BrokerServerAware {

    /**
     * set brokerServer
     * @param brokerServer brokerServer
     */
    void setBrokerServer(BrokerServer brokerServer);

}
