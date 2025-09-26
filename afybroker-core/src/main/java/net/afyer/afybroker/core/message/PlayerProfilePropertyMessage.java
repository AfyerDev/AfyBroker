package net.afyer.afybroker.core.message;

import java.io.Serializable;
import java.util.*;

/**
 * @author Nipuru
 * @since 2024/12/03 10:14
 */
public class PlayerProfilePropertyMessage implements Serializable {
    /** 玩家uuid */
    private UUID uniqueId;
    /** 需要移除的 Property */
    private List<String> removeList;
    /** 需要更新的 Property */
    private Map<String, String[]> updateMap;

    public UUID getUniqueId() {
        return uniqueId;
    }

    public PlayerProfilePropertyMessage setUniqueId(UUID uniqueId) {
        this.uniqueId = uniqueId;
        return this;
    }

    public List<String> getRemoveList() {
        return removeList;
    }

    public PlayerProfilePropertyMessage setRemoveList(List<String> removeList) {
        this.removeList = removeList;
        return this;
    }

    public Map<String, String[]> getUpdateMap() {
        return updateMap;
    }

    public PlayerProfilePropertyMessage setUpdateMap(Map<String, String[]> updateMap) {
        this.updateMap = updateMap;
        return this;
    }

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

    @Override
    public String toString() {
        return "PlayerProfilePropertyMessage{" +
                "uniqueId=" + uniqueId +
                ", removeList=" + removeList +
                ", updateMap=" + updateMap +
                '}';
    }
}
