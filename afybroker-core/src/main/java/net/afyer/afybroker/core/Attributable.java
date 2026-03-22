package net.afyer.afybroker.core;

import org.jetbrains.annotations.Nullable;

/**
 * 可属性化接口，提供基于键值对的属性存储能力
 * 值以 byte[] 形式存储（Hessian 序列化）
 *
 * @author Conan-Wen
 * @since 2026/3/22
 */
public interface Attributable {

    /**
     * 设置属性
     *
     * @param key   属性键
     * @param value 属性值（Hessian 序列化后的字节数组）
     */
    void setAttribute(String key, byte[] value);

    /**
     * 获取属性
     *
     * @param key 属性键
     * @return 属性值，不存在则返回 null
     */
    @Nullable
    byte[] getAttribute(String key);

    /**
     * 判断是否存在指定属性
     *
     * @param key 属性键
     * @return 是否存在
     */
    boolean hasAttribute(String key);

    /**
     * 移除属性
     *
     * @param key 属性键
     * @return 被移除的属性值，不存在则返回 null
     */
    @Nullable
    byte[] removeAttribute(String key);
}