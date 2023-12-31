package net.afyer.afybroker.bukkit.command;

import com.alipay.remoting.exception.RemotingException;
import net.afyer.afybroker.bukkit.AfyBroker;
import net.afyer.afybroker.core.BrokerClientType;
import net.afyer.afybroker.core.message.BroadcastChatMessage;
import org.apache.commons.lang.StringUtils;
import org.bukkit.command.CommandSender;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

/**
 * @author Nipuru
 * @since 2022/8/11 18:37
 */
public class BroadcastChatCommand extends AbstractTabExecutor {

    private final AfyBroker plugin;

    public BroadcastChatCommand(AfyBroker plugin) {
        this.plugin = plugin;
    }

    @Override
    public String getCommand() {
        return "broadcast";
    }

    @Override
    public boolean hasPermission(CommandSender sender) {
        return sender.hasPermission("afyer.afybroker.command.broadcast");
    }

    @Override
    public boolean canConsoleExecute() {
        return true;
    }

    @Override
    public boolean onCommand(CommandSender sender, String[] args) {
        if (args.length < 2) {
            sender.sendMessage("/broadcast <type> <message>");
            return true;
        }

        BrokerClientType clientType;
        try {
            clientType = BrokerClientType.valueOf(args[0].toUpperCase(Locale.ROOT));
        } catch (Exception e) {
            sender.sendMessage("invalid type");
            return true;
        }

        String message = StringUtils.join(args, ' ', 1, args.length);

        BroadcastChatMessage broadcastChatMessage = new BroadcastChatMessage()
                .setType(clientType)
                .setMessage(message);

        plugin.getServer().getScheduler().runTaskAsynchronously(plugin, () -> {
            try {
                plugin.getBrokerClient().oneway(broadcastChatMessage);
            } catch (RemotingException | InterruptedException e) {
                e.printStackTrace();
            }
        });
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, String[] args) {
        if (args.length == 1) {
            return Arrays.stream(BrokerClientType.values()).map(Enum::name).collect(Collectors.toList());
        }
        return Collections.emptyList();
    }
}
