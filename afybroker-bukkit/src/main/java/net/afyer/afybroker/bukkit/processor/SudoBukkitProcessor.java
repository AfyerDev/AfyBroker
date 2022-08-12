package net.afyer.afybroker.bukkit.processor;

import com.alipay.remoting.AsyncContext;
import com.alipay.remoting.BizContext;
import com.alipay.remoting.rpc.protocol.AsyncUserProcessor;
import net.afyer.afybroker.core.message.SudoMessage;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

/**
 * @author Nipuru
 * @since 2022/8/12 15:46
 */
public class SudoBukkitProcessor extends AsyncUserProcessor<SudoMessage> {

    private final Plugin plugin;

    public SudoBukkitProcessor(Plugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public void handleRequest(BizContext bizCtx, AsyncContext asyncCtx, SudoMessage request) {
        Player player = Bukkit.getPlayer(request.getPlayer());

        if (player == null) {
            return;
        }

        plugin.getServer().getScheduler().runTask(plugin, () -> Bukkit.dispatchCommand(player, request.getCommand()));
    }

    @Override
    public String interest() {
        return SudoMessage.class.getName();
    }
}
