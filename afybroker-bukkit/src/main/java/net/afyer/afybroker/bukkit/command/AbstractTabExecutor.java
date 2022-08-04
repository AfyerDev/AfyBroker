package net.afyer.afybroker.bukkit.command;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginCommand;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * @author Nipuru
 * @since 2022/7/12 15:58
 */
public abstract class AbstractTabExecutor implements TabExecutor {

    private final List<AbstractTabExecutor> subExecutors = new ArrayList<>();

    public void register(Plugin plugin) {
        PluginCommand pluginCommand = plugin.getServer().getPluginCommand(getCommand());

        if (pluginCommand == null) {
            throw new IllegalArgumentException("command '" + getCommand() + "' not registered!");
        }

        pluginCommand.setExecutor(this);
        pluginCommand.setTabCompleter(this);
    }

    public AbstractTabExecutor registerSubExecutors(AbstractTabExecutor... executors) {
        subExecutors.addAll(Arrays.asList(executors));
        return this;
    }

    public List<AbstractTabExecutor> getSubExecutors() {
        return subExecutors;
    }

    public boolean matchCommand(String command) {
        return getCommand().equalsIgnoreCase(command) || getAliases().stream().anyMatch(command::equalsIgnoreCase);
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!hasPermission(sender)) {
            sender.sendMessage("你没有权限");
            return true;
        }
        if (!canConsoleExecute() && !(sender instanceof Player)) {
            sender.sendMessage("只有玩家才能执行");
            return true;
        }
        if (args.length > 0) {
            for (AbstractTabExecutor executor : subExecutors) {
                if (executor.matchCommand(args[0])) {
                    return executor.onCommand(sender, command, label, Arrays.copyOfRange(args, 1, args.length));
                }
            }
        }
        return onCommand(sender, args);
    }

    @Nullable
    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {
        if (!hasPermission(sender)) {
            return Collections.emptyList();
        }
        if (args.length > 0) {
            for (AbstractTabExecutor executor : subExecutors) {
                if (executor.matchCommand(args[0])) {
                    return executor.onTabComplete(sender, command, alias, Arrays.copyOfRange(args, 1, args.length));
                }
            }
        }
        return onTabComplete(sender, args);
    }

    public abstract String getCommand();

    public List<String> getAliases() {
        return Collections.emptyList();
    }

    public abstract boolean hasPermission(CommandSender sender);

    public abstract boolean canConsoleExecute();

    public abstract boolean onCommand(CommandSender sender, String[] args);

    public abstract List<String> onTabComplete(CommandSender sender, String[] args);

}
