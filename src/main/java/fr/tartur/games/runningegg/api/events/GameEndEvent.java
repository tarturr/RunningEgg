package fr.tartur.games.runningegg.api.events;

import fr.tartur.games.runningegg.game.Game;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class GameEndEvent extends Event {

    private static final HandlerList HANDLER_LIST = new HandlerList();

    private final Game game;

    public GameEndEvent(Game game) {
        this.game = game;
    }

    public static HandlerList getHandlerList() {
        return HANDLER_LIST;
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return HANDLER_LIST;
    }

    public Game getGame() {
        return game;
    }

}
