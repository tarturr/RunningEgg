package fr.tartur.games.runningegg.listeners;

import io.papermc.paper.event.player.PlayerItemFrameChangeEvent;
import org.bukkit.entity.Chicken;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.hanging.HangingBreakByEntityEvent;
import org.bukkit.event.player.PlayerDropItemEvent;

/**
 * Class which listens to a list of events and ALWAYS acts the same way when any of them is triggered.
 */
public class PlayerInvariantListener implements Listener {

    /**
     * Event callback triggered when the player's food level changes (and cancels this event).
     *
     * @param event The event.
     */
    @EventHandler
    public void onFoodLose(FoodLevelChangeEvent event) {
        if (event.getEntity() instanceof Player) {
            event.setCancelled(true);
        }
    }

    /**
     * Event callback triggered when the player drops an item (and cancels the event).
     *
     * @param event The event.
     */
    @EventHandler
    public void onPlayerDrop(PlayerDropItemEvent event) {
        event.setCancelled(true);
    }

    /**
     * Event callback triggered when a player picks up an item (and cancels this event).
     *
     * @param event The event.
     */
    @EventHandler
    public void onPlayerPickupItem(EntityPickupItemEvent event) {
        if (event.getEntity() instanceof Player) {
            event.setCancelled(true);
        }
    }

    /**
     * Event callback triggered when the player is taking damage (and cancels this event).
     *
     * @param event The event.
     */
    @EventHandler
    public void onEntityDamaged(EntityDamageEvent event) {
        if (event.getEntity() instanceof Player) {
            event.setCancelled(true);
        }
    }

    /**
     * Event callback triggered when an {@link ItemFrame} is being broken (and cancels this event).
     *
     * @param event The event.
     */
    @EventHandler
    public void onItemFrameBroken(HangingBreakByEntityEvent event) {
        if (event.getEntity() instanceof ItemFrame) {
            event.setCancelled(true);
        }
    }
    
    @EventHandler
    public void onChickenSpawnFromEgg(CreatureSpawnEvent event) {
        if (event.getEntity() instanceof Chicken && event.getSpawnReason() == CreatureSpawnEvent.SpawnReason.EGG) {
            event.setCancelled(true);
        }
    }

    /**
     * Event callback triggered when the player interacts with an {@link ItemFrame} (and cancels this event).
     *
     * @param event The event.
     */
    @EventHandler
    public void onPlayerClickAtItemFrame(PlayerItemFrameChangeEvent event) {
        event.setCancelled(true);
    }
    
}
