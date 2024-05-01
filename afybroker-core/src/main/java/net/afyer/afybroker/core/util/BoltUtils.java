package net.afyer.afybroker.core.util;

import com.alipay.remoting.BizContext;
import com.alipay.remoting.Protocol;
import com.alipay.remoting.ProtocolCode;
import com.alipay.remoting.ProtocolManager;
import com.alipay.remoting.rpc.protocol.RpcProtocol;
import com.alipay.remoting.rpc.protocol.RpcProtocolManager;
import com.alipay.remoting.rpc.protocol.RpcProtocolV2;
import lombok.experimental.UtilityClass;

import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * @author Nipuru
 * @since 2022/8/11 11:57
 */
@UtilityClass
public class BoltUtils {

    private static final ProtocolCode[] PROTOCOL_CODES = new ProtocolCode[] {
            ProtocolCode.fromBytes(RpcProtocol.PROTOCOL_CODE),
            ProtocolCode.fromBytes(RpcProtocolV2.PROTOCOL_CODE)
    };

    public static void ensureRegistered() {
        try {
            // 确保调用了静态代码块方法
            Class.forName("com.alipay.remoting.rpc.RpcRemoting");
            boolean needRegister = false;
            for (ProtocolCode protocolCode : PROTOCOL_CODES) {
                if (needRegister) break;
                needRegister = Objects.isNull(ProtocolManager.getProtocol(protocolCode));
            }
            if (!needRegister) return;
            ensureUnregistered();
            RpcProtocolManager.initProtocols();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static void ensureUnregistered() {
        try {
            for (ProtocolCode protocolCode : PROTOCOL_CODES) {
                Protocol protocol = ProtocolManager.unRegisterProtocol(protocolCode.getFirstByte());
                if (protocol == null) continue;
                ExecutorService executor = protocol.getCommandHandler().getDefaultExecutor();
                executor.shutdown();

                try {
                    if (!executor.awaitTermination(60, TimeUnit.SECONDS)) {
                        executor.shutdownNow();
                    }
                } catch (InterruptedException e) {
                    executor.shutdownNow();
                }
            }
        } catch (RuntimeException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 判断请求是否有返回结果
     */
    public static boolean hasResponse(BizContext bizCtx) {
        if (bizCtx == null) return false;

        // 通过过期时间判断 过期时间大于0则有返回结果
        return bizCtx.getClientTimeout() > 0;
    }
}
