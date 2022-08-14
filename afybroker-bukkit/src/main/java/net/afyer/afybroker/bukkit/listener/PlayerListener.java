package net.afyer.afybroker.bukkit.listener;

import net.afyer.afybroker.bukkit.AfyBroker;
import net.afyer.afybroker.bukkit.api.event.AsyncPlayerConnectOtherEvent;
import net.afyer.afybroker.client.BrokerClient;
import net.afyer.afybroker.core.message.BrokerClientInfoMessage;
import net.afyer.afybroker.core.message.PlayerBungeeMessage;
import net.jodah.expiringmap.ExpirationPolicy;
import net.jodah.expiringmap.ExpiringMap;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByBlockEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.*;

import java.util.Collections;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * @author Nipuru
 * @since 2022/8/1 15:52
 */
public class PlayerListener extends AbstractListener {

    private final AfyBroker plugin;

    private final Set<UUID> preConnect = Collections.newSetFromMap(ExpiringMap
            .builder()
            .expiration(10L, TimeUnit.SECONDS)
            .expirationPolicy(ExpirationPolicy.CREATED)
            .build());

    public PlayerListener(AfyBroker plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {

        Player player = event.getPlayer();
        BrokerClient brokerClient = plugin.getBrokerClient();
        BrokerClientInfoMessage clientInfo = brokerClient.getClientInfo();

        plugin.getServer().getScheduler().runTaskAsynchronously(plugin, () -> {
            PlayerBungeeMessage msg = new PlayerBungeeMessage()
                    .setClientName(clientInfo.getName())
                    .setUid(player.getUniqueId())
                    .setName(player.getName())
                    .setState(PlayerBungeeMessage.State.JOIN);

            brokerClient.oneway(msg);
        });

        preConnect.remove(player.getUniqueId());
    }

    @EventHandler
    public void onConnectOther(AsyncPlayerConnectOtherEvent event) {
        preConnect.add(event.getPlayer().getUniqueId());
        event.getPlayer().saveData();
    }

    //@EventHandler //TODO 或许不重要
    public void onMove(PlayerMoveEvent event){//玩家移动时
        if(preConnect.contains(event.getPlayer().getUniqueId())){
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onDrop(PlayerDropItemEvent event){//玩家丢物品
        if(preConnect.contains(event.getPlayer().getUniqueId())){
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onBreak(BlockBreakEvent event){//玩家破坏方块
        if(preConnect.contains(event.getPlayer().getUniqueId())){
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onPlace(BlockPlaceEvent event){//玩家放置方块
        if(preConnect.contains(event.getPlayer().getUniqueId())){
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onSwap(PlayerSwapHandItemsEvent event){//玩家切换副手
        if(preConnect.contains(event.getPlayer().getUniqueId())){
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent event){//玩家与空气方块交互时
        if(preConnect.contains(event.getPlayer().getUniqueId())){
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onInteract(PlayerInteractEntityEvent event){//玩家点击实体时
        if(preConnect.contains(event.getPlayer().getUniqueId())){
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onDamaged(EntityDamageByEntityEvent event){//实体攻击实体
        if(preConnect.contains(event.getEntity().getUniqueId())){
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onDamaged(EntityDamageByBlockEvent event){//实体受到方块伤害
        if(preConnect.contains(event.getEntity().getUniqueId())){
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onPick(EntityPickupItemEvent event){//实体捡起物品
        if(preConnect.contains(event.getEntity().getUniqueId())){
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onClick(InventoryClickEvent event){//点击物品栏
        if(preConnect.contains(event.getWhoClicked().getUniqueId())){
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onChat(AsyncPlayerChatEvent event){//聊天
        if(preConnect.contains(event.getPlayer().getUniqueId())){
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onCommand(PlayerCommandPreprocessEvent event){//命令
        if(preConnect.contains(event.getPlayer().getUniqueId())){
            event.setCancelled(true);
        }
    }


}
