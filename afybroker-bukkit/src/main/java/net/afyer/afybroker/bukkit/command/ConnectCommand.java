package net.afyer.afybroker.bukkit.command;

import net.afyer.afybroker.bukkit.BukkitKit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Collections;
import java.util.List;

/**
 * @author Nipuru
 * @since 2022/8/3 19:05
 */
public class ConnectCommand extends AbstractTabExecutor {
    @Override
    public String getCommand() {
        return "connect";
    }

    @Override
    public boolean hasPermission(CommandSender sender) {
        return sender.hasPermission("afyer.afybroker.command.connect");
    }

    @Override
    public boolean canConsoleExecute() {
        return false;
    }

    @Override
    public boolean onCommand(CommandSender sender, String[] args) {
        Player player = (Player) sender;
        if (args.length != 1) {
            player.sendMessage("/connect <server>");
        }

        String server = args[0];

        BukkitKit.playerConnectOther(player, server);
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, String[] args) {
        return Collections.emptyList();
    }
}
