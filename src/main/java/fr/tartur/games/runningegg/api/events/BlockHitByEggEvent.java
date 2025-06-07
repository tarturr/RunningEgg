package fr.tartur.games.runningegg.api.events;

import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Egg;
import org.bukkit.event.HandlerList;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.jetbrains.annotations.NotNull;

/**
 * Class representing an event triggered when an egg hits a {@link Block}.
 */
public class BlockHitByEggEvent extends ProjectileHitEvent {
    
    private static final HandlerList HANDLER_LIST = new HandlerList();

    /**
     * Class constructor, which saves the {@link Egg} thrown in this event and the hit {@link Block} and its
     * {@link BlockFace}.
     * 
     * @param egg The thrown projectile.
     * @param hitBlock The hit block.
     * @param hitFace The block face hit by the projectile.
     */
    public BlockHitByEggEvent(Egg egg, Block hitBlock, BlockFace hitFace) {
        super(egg, null, hitBlock, hitFace);
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
}
