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

    /**
     * 环境变量：主机名
     */
    String ENV_HOSTNAME = "HOSTNAME";

    /**
     * 环境变量：客户端标签
     */
    String ENV_CLIENT_TAG = "AFYBROKER_CLIENT_TAG";

    /**
     * 环境变量：broker 地址
     */
    String ENV_BROKER_HOST = "AFYBROKER_HOST";

    /**
     * 环境变量：broker 端口
     */
    String ENV_BROKER_PORT = "AFYBROKER_PORT";


}
