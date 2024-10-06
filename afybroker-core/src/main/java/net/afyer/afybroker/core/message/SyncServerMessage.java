package net.afyer.afybroker.core.message;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;
import lombok.experimental.FieldDefaults;

import java.io.Serializable;
import java.util.Map;

/**
 * broker-server 发出，由 proxy 类型的服务器处理
 */
@Getter
@Setter
@ToString
@Accessors(chain = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
public class SyncServerMessage implements Serializable {

    /** 在线的 mc 服务器列表， key:name, value:address */
    Map<String, String> servers;
}
