package fr.tartur.games.runningegg.api.events;

import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;
import org.jetbrains.annotations.NotNull;

public class PlayerHitsWorldBorderEvent extends PlayerEvent {
    
    private static final HandlerList HANDLER_LIST = new HandlerList();

    public PlayerHitsWorldBorderEvent(@NotNull Player player) {
        super(player);
    }

    public static HandlerList getHandlerList() {
        return HANDLER_LIST;
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return HANDLER_LIST;
    }
    
}
