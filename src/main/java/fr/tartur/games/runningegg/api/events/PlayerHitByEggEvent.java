package fr.tartur.games.runningegg.api.events;

import org.bukkit.entity.Egg;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.jetbrains.annotations.NotNull;

/**
 * Class representing an event triggered when an egg hits a {@link Player}.
 */
public class PlayerHitByEggEvent extends ProjectileHitEvent {
    
    private static final HandlerList HANDLER_LIST = new HandlerList();

    /**
     * Class constructor, which saves the {@link Egg} thrown in this event and the hit {@link Player}.
     * 
     * @param egg The thrown projectile.
     * @param player The hit player.
     */
    public PlayerHitByEggEvent(Egg egg, Player player) {
        super(egg, player, null, null);
    }

    /**
     * The event's {@link HandlerList}.
     *
     * @return The said {@code HandlerList}.
     */
    public static @NotNull HandlerList getHandlerList() {
        return HANDLER_LIST;
    }

    /**
     * The event's {@link HandlerList}.
     *
     * @return The said {@code HandlerList}.
     */
    @Override
    public @NotNull HandlerList getHandlers() {
        return HANDLER_LIST;
    }

    /**
     * Gets the thrown {@link Egg} involved the event.
     * 
     * @return The said {@code Egg}.
     */
    @Override
    public @NotNull Egg getEntity() {
        return (Egg) super.getEntity();
    }

    /**
     * Gets the hit {@link Player}.
     * 
     * @return The said {@code Player}.
     */
    @Override
    public @NotNull Player getHitEntity() {
        final Player player = (Player) super.getHitEntity();
        
        if (player == null) {
            throw new NullPointerException("Initialized PlayerHitByEggEvent class with a null Player");
        }
        
        return player;
    }
    
}
