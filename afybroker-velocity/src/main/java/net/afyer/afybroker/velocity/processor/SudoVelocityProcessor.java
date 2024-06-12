package net.afyer.afybroker.velocity.processor;

import com.alipay.remoting.AsyncContext;
import com.alipay.remoting.BizContext;
import com.alipay.remoting.rpc.protocol.AsyncUserProcessor;
import com.velocitypowered.api.command.CommandManager;
import com.velocitypowered.api.proxy.ProxyServer;
import lombok.AllArgsConstructor;
import net.afyer.afybroker.core.message.SudoMessage;
import net.afyer.afybroker.velocity.AfyBroker;

/**
 * @author Nipuru
 * @since 2022/8/12 15:46
 */
@AllArgsConstructor
public class SudoVelocityProcessor extends AsyncUserProcessor<SudoMessage> {

    private final AfyBroker plugin;

    @Override
    public void handleRequest(BizContext bizCtx, AsyncContext asyncCtx, SudoMessage request) {
        ProxyServer server = plugin.getServer();
        CommandManager commandManager = server.getCommandManager();
        server.getPlayer(request.getPlayer())
                .ifPresent(player -> commandManager.executeAsync(player, request.getCommand()));
    }

    @Override
    public String interest() {
        return SudoMessage.class.getName();
    }
}
