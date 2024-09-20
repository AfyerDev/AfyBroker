package net.afyer.afybroker.core;

import lombok.experimental.UtilityClass;

/**
 * @author Nipuru
 * @since 2022/7/30 16:43
 */
@UtilityClass
public class BrokerGlobalConfig {

    /**
     * broker 默认地址
     */
    public final String BROKER_HOST = "localhost";

    /**
     * broker 默认端口
     */
    public final int BROKER_PORT = 11200;

    /**
     * bolt 默认消息发送超时时间
     */
    public final int DEFAULT_TIMEOUT_MILLIS = 3000;

}
