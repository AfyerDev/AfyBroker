package net.afyer.afybroker.core.message;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;
import lombok.experimental.FieldDefaults;

import java.io.Serializable;
import java.util.Set;
import java.util.UUID;

/**
 * RPC调用消息
 * 
 * @author Nipuru
 * @since 2025/7/11 18:04
 */
@Getter
@Setter
@ToString
@Accessors(chain = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
public class RpcInvocationMessage implements Serializable {
    private static final long serialVersionUID = 1L;
    
    /** 服务接口名 */
    String serviceInterface;
    
    /** 方法名 */
    String methodName;
    
    /** 参数类型 */
    String[] parameterTypes;
    
    /** 参数值 */
    byte[] parameters;
    
    /** 服务标签，用于服务选择 */
    Set<String> serviceTags;
} 