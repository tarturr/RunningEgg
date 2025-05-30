package fr.tartur.games.runningegg.listeners;

import fr.tartur.games.runningegg.api.events.PlayerHitsWorldBorderEvent;
import io.papermc.paper.event.player.PlayerItemFrameChangeEvent;
import org.bukkit.Location;
import org.bukkit.WorldBorder;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.hanging.HangingBreakByEntityEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerMoveEvent;

public class PlayerInvariantListener implements Listener {

    @EventHandler
    public void onFoodLose(FoodLevelChangeEvent event) {
        if (event.getEntity() instanceof Player) {
            event.setCancelled(true);
        }
    }
    
    @EventHandler
    public void onPlayerDrop(PlayerDropItemEvent event) {
        event.setCancelled(true);
    }
    
    @EventHandler
    public void onPlayerPickupItem(EntityPickupItemEvent event) {
        if (event.getEntity() instanceof Player) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onEntityDamaged(EntityDamageEvent event) {
        if (event.getEntity() instanceof Player) {
            event.setCancelled(true);
        }
    }
    
    @EventHandler
    public void onItemFrameBroken(HangingBreakByEntityEvent event) {
        if (event.getEntity() instanceof ItemFrame) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onPlayerClickAtItemFrame(PlayerItemFrameChangeEvent event) {
        event.setCancelled(true);
    }
    
    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        final Player player = event.getPlayer();
        final Location location = player.getLocation();
        final WorldBorder border = player.getWorldBorder();
        
        if (border == null) {
            return;
        }
        
        final Location center = border.getCenter();
        final double size = border.getSize() / 2f;
        
        final double distX = Math.abs(location.getX() - center.getX());
        final double distZ = Math.abs(location.getZ() - center.getZ());
        
        if (distX >= size || distZ >= size) {
            new PlayerHitsWorldBorderEvent(player).callEvent();
        }
    }
    
}
