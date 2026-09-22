package net.afyer.afybroker.core.message;

import java.io.Serializable;

/** error 为 null 表示该连接及其全部服务已注册成功。 */
public class BrokerClientRegistrationResult implements Serializable {
    private static final long serialVersionUID = 1L;

    private final String error;

    public BrokerClientRegistrationResult(String error) {
        this.error = error;
    }

    public String getError() {
        return error;
    }
}
