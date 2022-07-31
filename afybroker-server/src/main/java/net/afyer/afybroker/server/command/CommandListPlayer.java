package net.afyer.afybroker.server.command;

import net.afyer.afybroker.server.BrokerServer;
import net.afyer.afybroker.server.plugin.Command;
import net.afyer.afybroker.server.proxy.BrokerPlayer;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Nipuru
 * @since 2022/7/31 14:17
 */
public class CommandListPlayer extends Command {

    private final BrokerServer server;

    public CommandListPlayer(BrokerServer server) {
        super("listplayer");
        this.server = server;
    }

    @Override
    public void execute(String[] args) {
        List<BrokerPlayer> players = new ArrayList<>(server.getBrokerPlayerManager().getPlayers());

        for (BrokerPlayer player : players) {
            BrokerServer.getLogger().info("BrokerPlayer(Uid={}, bungee={}, bukkit={}",
                    player.getUid(), player.getBungeeProxy(), player.getBukkitServer());
        }
    }
}
