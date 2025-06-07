package fr.tartur.games.runningegg.api.events;

import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;
import org.jetbrains.annotations.NotNull;

/**
 * Event triggered when a {@link Player} hits a world border.
 */
public class PlayerHitsWorldBorderEvent extends PlayerEvent {
    
    private static final HandlerList HANDLER_LIST = new HandlerList();

    /**
     * Class constructor, which saves the provided {@link Player} who touched the world border.
     * 
     * @param player The involved player of the event.
     */
    public PlayerHitsWorldBorderEvent(@NotNull Player player) {
        super(player);
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
    
}
