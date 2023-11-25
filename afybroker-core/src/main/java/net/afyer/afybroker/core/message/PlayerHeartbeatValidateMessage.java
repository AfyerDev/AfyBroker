package net.afyer.afybroker.core.message;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;
import lombok.experimental.FieldDefaults;

import java.io.Serializable;
import java.util.List;
import java.util.UUID;

/**
 * 玩家心跳验证
 *
 * @author Nipuru
 * @since 2023/11/25 12:31
 */
@Getter
@Setter
@ToString
@Accessors(chain = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PlayerHeartbeatValidateMessage implements Serializable {
    private static final long serialVersionUID = -3464928414600404140L;

    /** 需要验证的玩家集合 */
    List<UUID> uniqueIdList;
}
