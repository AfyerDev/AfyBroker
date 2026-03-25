package net.afyer.afybroker.core;

import com.alipay.remoting.exception.CodecException;
import org.jetbrains.annotations.Nullable;

/**
 * 可属性化接口，提供基于键值对的属性存储能力
 *
 * @author Conan-Wen
 * @since 2026/3/22
 */
public interface Attributable {

    /**
     * 设置属性
     *
     * @param key   属性键
     * @param value 属性值
     */
    default <T> void setAttribute(String key, T value) throws CodecException {
        getAttributeContainer().setAttribute(key, value);
    }

    /**
     * 获取属性
     *
     * @param key 属性键
     * @return 属性值，不存在则返回 null
     */
    @Nullable
    default <T> T getAttribute(String key) throws CodecException {
        return getAttributeContainer().getAttribute(key);
    }

    /**
     * 判断是否存在指定属性
     *
     * @param key 属性键
     * @return 是否存在
     */
    default boolean hasAttribute(String key) {
        return getAttributeContainer().hasAttribute(key);
    }

    /**
     * 移除属性
     *
     * @param key 属性键
     * @return 被移除的属性值，不存在则返回 null
     */
    @Nullable
    default <T> T removeAttribute(String key) throws CodecException {
        return getAttributeContainer().removeAttribute(key);
    }

    /**
     * 获取属性容器
     *
     * @return 属性容器
     */
    AttributeContainer getAttributeContainer();
}