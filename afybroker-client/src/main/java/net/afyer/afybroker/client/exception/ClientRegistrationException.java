package net.afyer.afybroker.client.exception;

import com.alipay.remoting.exception.RemotingException;

/** 连接或注册客户端失败；客户端仍可在重连后重新注册。 */
public class ClientRegistrationException extends RemotingException {
    private static final long serialVersionUID = 1L;

    public ClientRegistrationException(String message) {
        super(message);
    }

    public ClientRegistrationException(String message, Throwable cause) {
        super(message, cause);
    }
}
