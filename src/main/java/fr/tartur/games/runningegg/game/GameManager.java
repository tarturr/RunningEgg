package fr.tartur.games.runningegg.game;

import fr.tartur.games.runningegg.api.events.GameEndEvent;
import fr.tartur.games.runningegg.api.events.GameStartEvent;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.logging.Logger;

public class GameManager implements Listener {

    private final Logger log;
    private final Set<Game> games;
    private final WaitingRoom waitingRoom;

    public GameManager(Logger log, WaitingRoom waitingRoom) {
        this.log = log;
        this.games = new HashSet<>();
        this.waitingRoom = waitingRoom;
    }

    @EventHandler
    public void onGameStart(GameStartEvent event) {
        final Game.Data data = event.getGameData();
        final Set<Player> players = data.players();
        final List<Location> spawnPoints = data.settings().playerLocations();

        final int playerCount = players.size();
        final int diff = playerCount - spawnPoints.size();

        if (diff > 0) {
            this.log.warning("Missing " + diff + " spawn points in config.yml to play with " + playerCount +
                    " players");
            return;
        }

        final Game game = new Game(this.log, data);
        this.games.add(game);
        game.start();
    }

    @EventHandler
    public void onGameEnd(GameEndEvent event) {
        this.games.remove(event.getGame());
    }

    public Optional<Game> getGameOfPlayer(Player player) {
        return this.games.stream()
                .filter(game -> game.isPresent(player))
                .findFirst();
    }

    public void join(Player player) {
        this.waitingRoom.join(player);
    }

    public void leave(Player player) {
        this.waitingRoom.leave(player);
    }

}
