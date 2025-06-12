package fr.tartur.games.runningegg.game;

import fr.tartur.games.runningegg.Core;
import fr.tartur.games.runningegg.api.events.GameEndEvent;
import fr.tartur.games.runningegg.api.events.GameStartEvent;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.List;
import java.util.logging.Logger;

/**
 * Class which handles every {@link Game} start & termination.
 *
 * @see Game
 */
public class GameManager implements Listener {

    private final Logger log;
    private final Core core;
    private final WaitingRoom waitingRoom;
    private Game game;

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

        this.game = new Game(core, data);
        this.core.getServer().getPluginManager().registerEvents(this.game, this.core);
        this.game.start();
    }

    /**
     * Event callback triggered when a running {@link Game} needs to stop.
     *
     * @param event The event.
     */
    @EventHandler
    public void onGameEnd(GameEndEvent event) {
        final Game game = event.getGame();
        
        game.unregisterEvents();
        this.game = null;
        
        for (final Player player : game.getPlayers().getAll()) {
            player.clearActivePotionEffects();
            player.teleport(game.getData().settings().spinLocation());
            player.setGameMode(GameMode.ADVENTURE);
            
            this.waitingRoom.join(player);
        }
    }

    /**
     * Checks whether the class {@link Game} is running.
     *
     * @return {@code true} if the {@code Game} instance is not null, {@code false} otherwise.
     */
    public boolean isGameRunning() {
        return this.game != null;
    }

    /**
     * Puts the provided player in the waiting room.
     *
     * @param player The player waiting for a new game to start.
     */
    public void join(Player player) {
        if (!this.isGameRunning()) {
            player.setGameMode(GameMode.ADVENTURE);
            player.getInventory().clear();
            player.clearActivePotionEffects();
            player.setLevel(0);
            player.setExp(0);
            this.waitingRoom.join(player);
        } else {
            this.game.connect(player);
        }
    }

    /**
     * Removes the provided player from the waiting room.
     *
     * @param player The leaving player.
     */
    public void leave(Player player) {
        if (this.isGameRunning() && this.game.isPresent(player)) {
            this.game.disconnect(player);
        } else {
            this.waitingRoom.leave(player);
        }
    }

}
