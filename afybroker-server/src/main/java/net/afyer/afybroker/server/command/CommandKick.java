package net.afyer.afybroker.server.command;

import com.alipay.remoting.exception.RemotingException;
import com.mojang.brigadier.LiteralMessage;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import net.afyer.afybroker.core.message.KickPlayerMessage;
import net.afyer.afybroker.server.BrokerServer;
import net.afyer.afybroker.server.plugin.BrigadierCommand;
import net.afyer.afybroker.server.proxy.BrokerPlayer;

import java.util.Comparator;

import static com.mojang.brigadier.arguments.StringArgumentType.getString;
import static com.mojang.brigadier.arguments.StringArgumentType.greedyString;
import static com.mojang.brigadier.arguments.StringArgumentType.word;

/**
 * @author Nipuru
 * @since 2022/10/10 10:41
 */
public class CommandKick implements BrigadierCommand {

    private static final SimpleCommandExceptionType USAGE_EXCEPTION =
            new SimpleCommandExceptionType(new LiteralMessage("Usage: kick <player> [message...]"));
    private static final DynamicCommandExceptionType PLAYER_NOT_ONLINE_EXCEPTION =
            new DynamicCommandExceptionType(player -> new LiteralMessage("Player not online: " + player));
    private static final DynamicCommandExceptionType KICK_FAILED_EXCEPTION =
            new DynamicCommandExceptionType(message -> new LiteralMessage("Kick failed: " + message));

    @Override
    public String getName() {
        return "kick";
    }

    @Override
    public String getUsage() {
        return "kick <player> [message...] - Kick an online player";
    }

    @Override
    public LiteralArgumentBuilder<BrokerServer> createBuilder() {
        return literal(getName())
                .executes(context -> { throw USAGE_EXCEPTION.create(); })
                .then(argument("player", word())
                        .suggests((context, builder) -> {
                            context.getSource().getPlayerManager().getPlayers().stream()
                                    .map(BrokerPlayer::getName)
                                    .sorted(Comparator.naturalOrder())
                                    .forEach(builder::suggest);
                            return builder.buildFuture();
                        })
                        .executes(context -> kick(context.getSource(), getString(context, "player"), ""))
                        .then(argument("message", greedyString())
                                .executes(context -> kick(
                                        context.getSource(),
                                        getString(context, "player"),
                                        getString(context, "message")
                                ))));
    }

    private int kick(BrokerServer server, String playerName, String message) throws CommandSyntaxException {
        BrokerPlayer player = server.getPlayer(playerName);
        if (player == null) {
            throw PLAYER_NOT_ONLINE_EXCEPTION.create(playerName);
        }

        KickPlayerMessage kickMessage = new KickPlayerMessage()
                .setUniqueId(player.getUniqueId())
                .setMessage(message);

        try {
            player.getProxy().oneway(kickMessage);
            return 1;
        } catch (RemotingException | InterruptedException e) {
            throw KICK_FAILED_EXCEPTION.create(e.getMessage() == null ? e.getClass().getSimpleName() : e.getMessage());
        }
    }
}
