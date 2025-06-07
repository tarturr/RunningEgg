package fr.tartur.games.runningegg.api.events;

import org.bukkit.WorldBorder;
import org.bukkit.entity.Entity;
import org.bukkit.event.HandlerList;
import org.bukkit.event.entity.EntityEvent;
import org.jetbrains.annotations.NotNull;

/**
 * Class representing an event triggered when an entity hits (or crosses) the {@link WorldBorder}.
 */
public class EntityHitsWorldBorderEvent extends EntityEvent {
    
    private static final HandlerList HANDLER_LIST = new HandlerList();
    
    private final WorldBorder worldBorder;

    /**
     * Class constructor, which saves the entity touching the provided {@link WorldBorder}.
     *
     * @param entity The involved entity of the event.
     * @param worldBorder The touched {@code WorldBorder}.
     */
    public EntityHitsWorldBorderEvent(@NotNull Entity entity, WorldBorder worldBorder) {
        super(entity);
        this.worldBorder = worldBorder;
    }
    
    /**
     * The event's {@link HandlerList}.
     *
     * @return The said {@code HandlerList}.
     */
    public static HandlerList getHandlerList() {
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
     * Returns the touched {@link WorldBorder} involved in this event.
     * 
     * @return The said {@code WorldBorder};
     */
    public WorldBorder getWorldBorder() {
        return worldBorder;
    }
    
}
