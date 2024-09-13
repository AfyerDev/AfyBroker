package net.afyer.afybroker.core.message;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;
import lombok.experimental.FieldDefaults;
import net.afyer.afybroker.core.BrokerClientInfo;

import java.io.Serializable;
import java.util.Map;
import java.util.Set;

/**
 * @author Nipuru
 * @since 2022/7/30 16:25
 */
@Getter
@Setter
@ToString
@Accessors(chain = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
public class BrokerClientInfoMessage implements Serializable {
    private static final long serialVersionUID = 5964124139341528361L;

    /** 客户端名称(唯一标识) */
    String name;
    /** 客户端标签 */
    Set<String> tags;
    /** 客户端类型 */
    String type;
    /** 服务器/客户端 地址 */
    String address;
    /** 客户端元数据 */
    Map<String, String> metadata;

    public BrokerClientInfo build() {
        return new BrokerClientInfo(name, tags, type, address, metadata);
    }

}
