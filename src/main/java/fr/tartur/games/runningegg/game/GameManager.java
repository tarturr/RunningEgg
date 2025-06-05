package fr.tartur.games.runningegg.game;

import fr.tartur.games.runningegg.Core;
import fr.tartur.games.runningegg.api.events.GameEndEvent;
import fr.tartur.games.runningegg.api.events.GameStartEvent;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.logging.Logger;

/**
 * Class which handles every {@link Game} start & termination.
 *
 * @see Game
 */
public class GameManager implements Listener {

    private final Logger log;
    private final Core core;
    private final Set<Game> games;
    private final WaitingRoom waitingRoom;

    /**
     * Class constructor which needs the {@link Core} plugin instance to register game events, and the
     * {@link WaitingRoom} to handle every player waiting for a new {@link Game} to start.
     *
     * @param core The main plugin instance.
     * @param waitingRoom The said waiting room.
     */
    public GameManager(Core core, WaitingRoom waitingRoom) {
        this.log = core.getLogger();
        this.core = core;
        this.games = new HashSet<>();
        this.waitingRoom = waitingRoom;
    }

    /**
     * Event callback triggered when a new {@link Game} needs to start.
     *
     * @param event The event.
     */
    @EventHandler
    public void onGameStart(GameStartEvent event) {
        final Game.Data data = event.getGameData();
        final List<Player> players = data.players();
        final List<Location> spawnPoints = data.settings().playerLocations();

        final int playerCount = players.size();
        final int diff = playerCount - spawnPoints.size();

        if (diff > 0) {
            this.log.warning("Missing " + diff + " spawn points in config.yml to play with " + playerCount +
                    " players");
            return;
        }

        final Game game = new Game(core, data);
        this.games.add(game);
        this.core.getServer().getPluginManager().registerEvents(game, this.core);
        game.start();
    }

    /**
     * Event callback triggered when a running {@link Game} needs to stop.
     *
     * @param event The event.
     */
    @EventHandler
    public void onGameEnd(GameEndEvent event) {
        final Game game = event.getGame();
        
        game.kickAll(Component.text("Reconnecte-toi pour jouer à nouveau ! :)", NamedTextColor.GREEN));
        game.unregisterEvents();
        this.games.remove(game);
    }

    /**
     * Gets the provided {@link Game} where the player lastly was seen wrapped in an {@link Optional} if it exists, or
     * {@link Optional#empty()} if the game already ended.
     *
     * @param player The player trying to join back its party.
     * @return An {@code Optional} wrapping a {@code Game} instance if the player's game is still running, or
     * {@code Optional.empty()} otherwise.
     */
    public Optional<Game> getGameOfPlayer(Player player) {
        return this.games.stream()
                .filter(game -> game.isPresent(player))
                .findFirst();
    }

    /**
     * Puts the provided player in the waiting room.
     *
     * @param player The player waiting for a new game to start.
     */
    public void join(Player player) {
        this.waitingRoom.join(player);
    }

    /**
     * Removes the provided player from the waiting room.
     *
     * @param player The leaving player.
     */
    public void leave(Player player) {
        this.waitingRoom.leave(player);
    }

}
