package net.afyer.afybroker.server.command;

import com.alipay.remoting.exception.RemotingException;
import lombok.extern.slf4j.Slf4j;
import net.afyer.afybroker.core.message.KickPlayerMessage;
import net.afyer.afybroker.server.BrokerServer;
import net.afyer.afybroker.server.plugin.Command;
import net.afyer.afybroker.server.proxy.BrokerPlayer;

/**
 * @author Nipuru
 * @since 2022/10/10 10:41
 */
@Slf4j
public class CommandKick extends Command {

    private final BrokerServer server;
    public CommandKick(BrokerServer server) {
        super("kick");
        this.server = server;
    }

    @Override
    public void execute(String[] args) {
        if (args.length == 0) {
            log.info("kick <player> [message...]");
            return;
        }

        String playerName = args[0];

        BrokerPlayer player = server.getPlayer(playerName);
        if (player == null) {
            log.info("player not online!");
            return;
        }

        StringBuilder messageBuilder = new StringBuilder();
        for (int i = 1; i < args.length; i++) {
            messageBuilder.append(args[i]);
        }

        KickPlayerMessage message = new KickPlayerMessage()
                .setUniqueId(player.getUniqueId())
                .setMessage(messageBuilder.toString());

        try {
            player.getProxy().oneway(message);
        } catch (RemotingException | InterruptedException e) {
            log.error(e.getMessage(), e);
        }
    }
}
