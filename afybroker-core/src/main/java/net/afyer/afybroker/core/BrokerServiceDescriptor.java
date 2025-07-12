package net.afyer.afybroker.core;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;
import lombok.experimental.FieldDefaults;

import java.io.Serializable;
import java.util.Map;
import java.util.Set;

/**
 * 服务描述符
 *
 * @author Nipuru
 * @since 2025/7/11 17:04
 */
@Getter
@Setter
@ToString
@Accessors(chain = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
public class BrokerServiceDescriptor implements Serializable {
    private static final long serialVersionUID = 1L;

    /** 服务接口名 */
    String serviceInterface;

    /** 服务标签 */
    Set<String> tags;
} 