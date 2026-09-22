package net.afyer.afybroker.client.exception;

import com.alipay.remoting.exception.RemotingException;

/** 本次注册被拒绝；客户端仍可在重连后重新注册。 */
public class ClientRegistrationException extends RemotingException {
    private static final long serialVersionUID = 1L;

    public ClientRegistrationException(String message) {
        super(message);
    }
}
