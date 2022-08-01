package net.afyer.afybroker.core.message;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;
import lombok.experimental.FieldDefaults;
import net.afyer.afybroker.core.BrokerClientType;

import java.io.Serial;
import java.io.Serializable;

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
    @Serial
    private static final long serialVersionUID = 5964124139341528361L;

    /** 客户端名称(唯一标识) */
    String name;
    /** 客户端标签 */
    String tag;
    /** 客户端类型 */
    BrokerClientType type;
    /** 客户端地址 */
    String address;

}
