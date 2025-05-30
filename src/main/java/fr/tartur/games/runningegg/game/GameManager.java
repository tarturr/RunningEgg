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

public class GameManager implements Listener {

    private final Logger log;
    private final Core core;
    private final Set<Game> games;
    private final WaitingRoom waitingRoom;

    public GameManager(Core core, WaitingRoom waitingRoom) {
        this.log = core.getLogger();
        this.core = core;
        this.games = new HashSet<>();
        this.waitingRoom = waitingRoom;
    }

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

    @EventHandler
    public void onGameEnd(GameEndEvent event) {
        final Game game = event.getGame();
        
        game.kickAll(Component.text("Reconnecte-toi pour jouer à nouveau ! :)", NamedTextColor.GREEN));
        game.unregisterEvents();
        this.games.remove(game);
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
