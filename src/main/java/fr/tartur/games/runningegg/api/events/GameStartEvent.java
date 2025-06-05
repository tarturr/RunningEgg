package fr.tartur.games.runningegg.api.events;

import fr.tartur.games.runningegg.game.Game;
import fr.tartur.games.runningegg.game.GameSettings;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * Event triggered on a {@link Game} start.
 */
public class GameStartEvent extends Event {

    private static final HandlerList HANDLER_LIST = new HandlerList();

    private final Game.Data gameData;

    /**
     * Class constructor, which needs a {@code List<Player>} and a {@link GameSettings} instance to load a new
     * {@link Game} instance.
     *
     * @param players The list of game players.
     * @param settings The game settings.
     */
    public GameStartEvent(List<Player> players, GameSettings settings) {
        this.gameData = new Game.Data(players, settings);
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
     * The {@link Game.Data} instance associated to the starting {@link Game}.
     *
     * @return The said instance.
     */
    public Game.Data getGameData() {
        return gameData;
    }

}
