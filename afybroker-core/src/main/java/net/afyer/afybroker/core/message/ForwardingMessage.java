package net.afyer.afybroker.core.message;

import java.io.Serializable;

/**
 * 用于封装消息 由client发送 然后由server转发给指定名称的client
 * @author Nipuru
 * @since 2022/9/4 18:15
 */
public abstract class ForwardingMessage implements Serializable {
    private static final long serialVersionUID = 7011303487498190975L;

    /** 目标broker client 名称 */
    private String clientName;

    /** 封装的消息 */
    private Serializable message;

    public String getClientName() {
        return clientName;
    }

    public ForwardingMessage setClientName(String clientName) {
        this.clientName = clientName;
        return this;
    }

    public Serializable getMessage() {
        return message;
    }

    public ForwardingMessage setMessage(Serializable message) {
        this.message = message;
        return this;
    }

    @Override
    public String toString() {
        return "ForwardingMessage{" +
                "clientName='" + clientName + '\'' +
                ", message=" + message +
                '}';
    }
}
