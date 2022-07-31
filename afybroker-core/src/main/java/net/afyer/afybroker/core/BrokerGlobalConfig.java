package net.afyer.afybroker.core;

import lombok.experimental.UtilityClass;

/**
 * @author Nipuru
 * @since 2022/7/30 16:43
 */
@UtilityClass
public class BrokerGlobalConfig {

    /** broker 默认端口 */
    public int brokerPort = 11200;

    /** bolt 消息发送超时时间 */
    public int timeoutMillis = 3000;

    /** true 开启日志 */
    public boolean openLog = true;

}
