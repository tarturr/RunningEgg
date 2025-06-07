package fr.tartur.games.runningegg.listeners;

import fr.tartur.games.runningegg.api.events.BlockHitByEggEvent;
import fr.tartur.games.runningegg.api.events.EntityHitsWorldBorderEvent;
import fr.tartur.games.runningegg.api.events.PlayerHitByEggEvent;
import fr.tartur.games.runningegg.api.events.PlayerHitsWorldBorderEvent;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.WorldBorder;
import org.bukkit.entity.Egg;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashSet;
import java.util.Set;

/**
 * Class listening to any type of {@link Event} which needs to be handled once in a certain amount of time.
 */
public class DelayedEventListener implements Listener {

    private final JavaPlugin plugin;
    private final Set<Class<? extends Event>> cooldown;
    
    private int task;

    /**
     * Class constructor, which needs a {@link JavaPlugin} instance for the delayed events.
     * 
     * @param plugin The main plugin instance.
     */
    public DelayedEventListener(JavaPlugin plugin) {
        this.plugin = plugin;
        this.cooldown = new HashSet<>();
    }

    /**
     * Event callback triggered when a player is moving, just to know if it is touching the {@link WorldBorder}. If
     * so, a {@link PlayerHitsWorldBorderEvent} is fired if the delay of 10 ticks has reached its end.
     *
     * @param event The event.
     */
    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        final Player player = event.getPlayer();
        final WorldBorder border = player.getLocation().getWorld().getWorldBorder();

        if (this.hitsWorldBorder(player, border)) {
            this.call(new PlayerHitsWorldBorderEvent(player));
        }
    }
    
    /**
     * Event callback triggered when an entity is moving, just to know if it is touching the {@link WorldBorder}. If
     * so, an {@link EntityHitsWorldBorderEvent} is fired if the delay of 10 ticks has reached its end.
     *
     * @param event The event.
     */
    @EventHandler
    public void onEggLaunch(ProjectileLaunchEvent event) {
        if (!(event.getEntity() instanceof final Egg egg)) {
            return;
        }
        
        final WorldBorder border = egg.getLocation().getWorld().getWorldBorder();
        
        this.task = Bukkit.getScheduler().scheduleSyncRepeatingTask(this.plugin, () -> {
            if (egg.isDead()) {
                Bukkit.getScheduler().cancelTask(this.task);
                return;
            }
            
            if (this.hitsWorldBorder(egg, border)) {
                new EntityHitsWorldBorderEvent(egg, border).callEvent();
                egg.remove();
            }
        }, 0L, 5L);

        if (this.hitsWorldBorder(egg, border)) {
            this.call(new EntityHitsWorldBorderEvent(egg, border));
        }
    }

    /**
     * Event callback triggered when an {@link Egg} hits a block or a player.
     *
     * @param event The event.
     */
    @EventHandler
    public void onHitByEgg(ProjectileHitEvent event) {
        if (event.getEntity() instanceof Egg egg) {
            if (event.getHitEntity() instanceof Player player) {
                this.call(new PlayerHitByEggEvent(egg, player));
            } else {
                this.call(new BlockHitByEggEvent(egg, event.getHitBlock(), event.getHitBlockFace()));
            }
        }
    }

    /**
     * Checks whether the given {@link Event} to call is NOT in the event cooldown set.
     * 
     * @param event The event to check.
     * @return {@code true} if the event can be called, {@code false} otherwise.
     */
    private boolean isCallable(Event event) {
        return !this.cooldown.contains(event.getClass());
    }

    /**
     * Calls {@link Event#callEvent()} on the provided {@link Event} if the associated delay of 10 ticks has reached its
     * end.
     * 
     * @param event The event to fire.
     */
    private void call(Event event) {
        if (!this.isCallable(event)) {
            return;
        }
        
        final Class<? extends Event> clazz = event.getClass();
        
        event.callEvent();
        this.cooldown.add(clazz);
        Bukkit.getScheduler().scheduleSyncDelayedTask(this.plugin, () -> this.cooldown.remove(clazz), 10L);
    }

    /**
     * Checks whether the provided {@link Entity} is touching (or has crossed) the {@link WorldBorder}.
     * 
     * @param entity The concerned entity.
     * @param border The world border.
     * @return {@code true} if the entity is touching the world border, {@code false} otherwise.
     */
    private boolean hitsWorldBorder(Entity entity, WorldBorder border) {
        final Location location = entity.getLocation();
        final Location center = border.getCenter();
        final double size = border.getSize() / 2f;

        final double distX = Math.abs(location.getX() - center.getX());
        final double distZ = Math.abs(location.getZ() - center.getZ());

        return distX >= size || distZ >= size;
    }
    
}
