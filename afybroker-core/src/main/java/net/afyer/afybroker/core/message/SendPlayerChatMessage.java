package net.afyer.afybroker.core.message;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;
import lombok.experimental.FieldDefaults;

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

    UUID uid;
    MessageType type;
    String message;

    public enum MessageType {
        BUKKIT,
        BUNGEE
    }
}
