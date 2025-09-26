package net.afyer.afybroker.core;


/**
 * @author Nipuru
 * @since 2022/7/30 16:43
 */
public interface BrokerGlobalConfig {

    /**
     * broker 默认地址
     */
    String BROKER_HOST = "localhost";

    /**
     * broker 默认端口
     */
    int BROKER_PORT = 11200;

    /**
     * bolt 默认消息发送超时时间
     */
    int DEFAULT_TIMEOUT_MILLIS = 3000;

}
