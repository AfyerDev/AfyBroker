package net.afyer.afybroker.server.command;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import net.afyer.afybroker.core.BrokerClientType;
import net.afyer.afybroker.server.Broker;
import net.afyer.afybroker.server.BrokerServer;
import net.afyer.afybroker.server.plugin.Command;
import net.afyer.afybroker.server.proxy.BrokerClientItem;
import net.afyer.afybroker.server.proxy.BrokerPlayer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * @author Nipuru
 * @since 2022/7/31 14:17
 */
public class CommandListPlayer extends Command {

    private static final Logger LOGGER = LoggerFactory.getLogger(CommandListPlayer.class);

    private final BrokerServer server;

    public CommandListPlayer(BrokerServer server) {
        super("glist");
        this.server = server;
    }

    @Override
    public void execute(String[] args) {
        List<BrokerPlayer> players = new ArrayList<>(server.getPlayerManager().getPlayers());

        Map<String, TreeSet<BrokerPlayer>> bungeeMap = Maps.newTreeMap();
        Broker.getClientManager().getByType(BrokerClientType.PROXY)
                .forEach(client -> bungeeMap.put(client.getName(), Sets.newTreeSet(Comparator.comparing(BrokerPlayer::getName))));
        for (BrokerPlayer player : players) {
            String bungeeName = player.getProxy().getName();
            bungeeMap.get(bungeeName).add(player);
        }

        bungeeMap.forEach((bungee, set) -> {
            StringBuilder builder = new StringBuilder();
            builder.append("[").append(bungee).append("]").append(" ")
                    .append("(").append(set.size()).append("):").append(" ");

            for (BrokerPlayer player : set) {
                builder.append(player.getName());
                BrokerClientItem bukkit = player.getServer();
                if (bukkit != null) {
                    builder.append("(").append(bukkit.getName()).append(")");
                }
                builder.append(" ");
            }
            LOGGER.info(builder.toString());
        });
        LOGGER.info(String.format("Total players online: %d", players.size()));
    }
}
