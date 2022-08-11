package net.afyer.afybroker.core.message;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;
import lombok.experimental.FieldDefaults;
import net.afyer.afybroker.core.ChatHandlerType;

import java.io.Serial;
import java.io.Serializable;
import java.util.UUID;

/**
 * @author Nipuru
 * @since 2022/8/5 10:04
 */
@Getter
@Setter
@ToString
@Accessors(chain = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
public class SendPlayerChatMessage implements Serializable {
    @Serial
    private static final long serialVersionUID = -4207075992906096144L;

    /**
     * 玩家uid
     */
    UUID uid;

    /**
     * 消息处理端的类型
     */
    ChatHandlerType type = ChatHandlerType.BUKKIT;

    /**
     * 消息
     */
    String message;

}
