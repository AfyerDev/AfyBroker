package net.afyer.afybroker.client.aware;

import net.afyer.afybroker.client.BrokerClient;

/**
 * BrokerClientAware
 * <p>
 *     设置 BrokerClient
 *     实例实现了该接口的对象通过{@link BrokerClient#aware(Object)} 即可设置实例
 * </p>
 * @author Nipuru
 * @since 2022/7/31 19:58
 */
public interface BrokerClientAware {

    /**
     * set brokerClient
     * @param brokerClient brokerClient
     */
    void setBrokerClient(BrokerClient brokerClient);

}
