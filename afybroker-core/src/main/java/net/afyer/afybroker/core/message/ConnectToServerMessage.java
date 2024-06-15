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
 * 让玩家连接到某个 bukkit 服务器
 *
 * @author Nipuru
 * @since 2022/9/6 17:33
 */
@Getter
@Setter
@ToString
@Accessors(chain = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ConnectToServerMessage implements Serializable {
    private static final long serialVersionUID = -2031147618861482881L;

    /** 玩家uid */
    UUID player;

    /** bukkit 服务器名（在 bungee 中的名字） */
    String server;

}
