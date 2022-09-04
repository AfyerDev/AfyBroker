package net.afyer.afybroker.core.message;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;
import lombok.experimental.FieldDefaults;

import java.io.Serial;
import java.io.Serializable;

/**
 * 用于封装消息 由client发送 然后由server转发给指定名称的client
 * @author Nipuru
 * @since 2022/9/4 18:15
 */
@Getter
@Setter
@ToString
@Accessors(chain = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ForwardingMessageWrapper implements Serializable {
    @Serial
    private static final long serialVersionUID = 7011303487498190975L;

    /** 目标broker client 名称 */
    String clientName;

    /** 封装的消息 */
    Serializable message;

    /** 是否有回调（sync callback future） */
    boolean hasResponse = false;
}
