package fr.tartur.games.runningegg.api.events;

import fr.tartur.games.runningegg.game.Game;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

/**
 * Event triggered on a {@link Game} ending.
 */
public class GameEndEvent extends Event {

    private static final HandlerList HANDLER_LIST = new HandlerList();

    private final Game game;

    /**
     * Class constructor, which keeps the game instance in memory.
     *
     * @param game The game instance.
     */
    public GameEndEvent(Game game) {
        this.game = game;
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
     * The ended {@link Game} instance.
     *
     * @return The game instance.
     */
    public Game getGame() {
        return game;
    }

}
