package net.afyer.afybroker.core.message;

import java.io.Serializable;
import java.util.Arrays;
import java.util.UUID;

/**
 * 属性操作统一消息
 * 通过 action 和 scope 字段区分不同操作类型和作用域
 *
 * @author Conan-Wen
 * @since 2026/3/22
 */
public class AttributeMessage implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 操作类型常量
     */
    public static final int ACTION_SET = 0;
    public static final int ACTION_GET = 1;
    public static final int ACTION_REMOVE = 2;
    public static final int ACTION_HAS = 3;

    /**
     * 作用域常量
     */
    public static final int SCOPE_SERVER = 0;
    public static final int SCOPE_PLAYER = 1;

    /**
     * 操作类型
     */
    private int action;

    /**
     * 作用域
     */
    private int scope;

    /**
     * 属性键
     */
    private String key;

    /**
     * 属性值（Hessian 序列化后的字节数组，仅 SET 时使用）
     */
    private byte[] value;

    /**
     * 玩家UUID（仅 SCOPE_PLAYER 时使用）
     */
    private UUID uniqueId;

    public int getAction() {
        return action;
    }

    public AttributeMessage setAction(int action) {
        this.action = action;
        return this;
    }

    public int getScope() {
        return scope;
    }

    public AttributeMessage setScope(int scope) {
        this.scope = scope;
        return this;
    }

    public String getKey() {
        return key;
    }

    public AttributeMessage setKey(String key) {
        this.key = key;
        return this;
    }

    public byte[] getValue() {
        return value;
    }

    public AttributeMessage setValue(byte[] value) {
        this.value = value;
        return this;
    }

    public UUID getUniqueId() {
        return uniqueId;
    }

    public AttributeMessage setUniqueId(UUID uniqueId) {
        this.uniqueId = uniqueId;
        return this;
    }

    @Override
    public String toString() {
        return "AttributeMessage{" +
                "action=" + action +
                ", scope=" + scope +
                ", key='" + key + '\'' +
                ", value=" + Arrays.toString(value) +
                ", uniqueId=" + uniqueId +
                '}';
    }
}
