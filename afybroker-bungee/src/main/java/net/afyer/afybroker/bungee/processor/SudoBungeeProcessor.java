package net.afyer.afybroker.bungee.processor;

import com.alipay.remoting.AsyncContext;
import com.alipay.remoting.BizContext;
import com.alipay.remoting.rpc.protocol.AsyncUserProcessor;
import net.afyer.afybroker.core.message.SudoMessage;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;

/**
 * @author Nipuru
 * @since 2022/8/12 15:46
 */
public class SudoBungeeProcessor extends AsyncUserProcessor<SudoMessage> {

    @Override
    public void handleRequest(BizContext bizCtx, AsyncContext asyncCtx, SudoMessage request) {
        ProxiedPlayer bungeePlayer = ProxyServer.getInstance().getPlayer(request.getPlayer());

        if (bungeePlayer == null) {
            return;
        }

        ProxyServer.getInstance().getPluginManager().dispatchCommand(bungeePlayer, request.getCommand());
    }

    @Override
    public String interest() {
        return SudoMessage.class.getName();
    }
}
