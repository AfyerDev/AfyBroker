package net.afyer.afybroker.core;

import com.alipay.remoting.config.ConfigManager;
import com.alipay.remoting.exception.CodecException;
import com.alipay.remoting.serialization.Serializer;
import com.alipay.remoting.serialization.SerializerManager;

import javax.annotation.Nullable;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class AttributeContainer {

    private final Map<String, byte[]> attributes = new ConcurrentHashMap<>();


    public <T> void setAttribute(String key, T value) throws CodecException {
        attributes.put(key, getSerializer().serialize(value));
    }

    @Nullable
    public <T> T getAttribute(String key) throws CodecException {
        return getSerializer().deserialize(attributes.get(key), Object.class.getName());
    }

    public boolean hasAttribute(String key) {
        return attributes.containsKey(key);
    }

    @Nullable
    public <T> T removeAttribute(String key) throws CodecException {
        return getSerializer().deserialize(attributes.remove(key), Object.class.getName());
    }

    public Map<String, byte[]> getRawAttributes() {
        return attributes;
    }

    public Serializer getSerializer() {
        return SerializerManager.getSerializer(ConfigManager.serializer());
    }
}
