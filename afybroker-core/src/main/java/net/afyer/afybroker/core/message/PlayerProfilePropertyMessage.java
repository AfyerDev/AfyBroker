package net.afyer.afybroker.core.message;


import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;
import lombok.experimental.FieldDefaults;

import java.io.Serializable;
import java.util.*;

/**
 * @author Nipuru
 * @since 2024/12/03 10:14
 */
@Getter
@Setter
@ToString
@Accessors(chain = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PlayerProfilePropertyMessage implements Serializable {
    /** 玩家uuid */
    UUID uniqueId;
    /** 需要移除的 Property */
    List<String> removeList;
    /** 需要更新的 Property */
    Map<String, String[]> updateMap;

    public PlayerProfilePropertyMessage remove(String name) {
        if (removeList == null) {
            removeList = new ArrayList<>();
        }
        removeList.add(name);
        return this;
    }

    public PlayerProfilePropertyMessage update(String name, String value, String signature) {
        if (updateMap == null) {
            updateMap = new HashMap<>();
        }
        updateMap.put(name, new String[]{value, signature});
        return this;
    }
}
