package net.afyer.afybroker.server.command;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import net.afyer.afybroker.core.BrokerClientType;
import net.afyer.afybroker.server.Broker;
import net.afyer.afybroker.server.BrokerServer;
import net.afyer.afybroker.server.plugin.BrigadierCommand;
import net.afyer.afybroker.server.proxy.BrokerClientItem;
import net.afyer.afybroker.server.proxy.BrokerPlayer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;

/**
 * @author Nipuru
 * @since 2022/7/31 14:17
 */
public class CommandListPlayer implements BrigadierCommand {

    private static final Logger LOGGER = LoggerFactory.getLogger(CommandListPlayer.class);

    @Override
    public String getName() {
        return "glist";
    }

    @Override
    public String getUsage() {
        return "glist - List online players grouped by proxy";
    }

    @Override
    public LiteralArgumentBuilder<BrokerServer> createBuilder() {
        return LiteralArgumentBuilder.<BrokerServer>literal(getName())
                .executes(context -> {
                    BrokerServer server = context.getSource();
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
                    LOGGER.info("Total players online: {}", players.size());
                    return 1;
                });
    }
}
