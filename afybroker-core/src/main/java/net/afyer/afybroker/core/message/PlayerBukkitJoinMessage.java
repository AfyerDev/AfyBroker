package net.afyer.afybroker.core.message;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;
import lombok.experimental.FieldDefaults;

import java.io.Serializable;
import java.util.UUID;

/**
 * @author Nipuru
 * @since 2023/09/29 12:04
 */
@Getter
@Setter
@ToString
@Accessors(chain = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PlayerBukkitJoinMessage implements Serializable {
    private static final long serialVersionUID = -1132388839270494188L;

    /** 玩家uniqueId */
    UUID uniqueId;

    /** 玩家名 */
    String name;

}
