package fr.tartur.games.runningegg.api.events;

import fr.tartur.games.runningegg.game.Game;
import fr.tartur.games.runningegg.game.GameSettings;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

import java.util.Set;

public class GameStartEvent extends Event {

    private static final HandlerList HANDLER_LIST = new HandlerList();

    private final Game.Data gameData;

    public GameStartEvent(Set<Player> players, GameSettings settings) {
        this.gameData = new Game.Data(players, settings);
    }

    public static HandlerList getHandlerList() {
        return HANDLER_LIST;
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return HANDLER_LIST;
    }

    public Game.Data getGameData() {
        return gameData;
    }

}
