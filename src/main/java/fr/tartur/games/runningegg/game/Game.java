package fr.tartur.games.runningegg.game;

import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;

import java.util.Set;
import java.util.logging.Logger;

public class Game {

    public record Data(Set<Player> players, GameSettings settings) {}

    private final Logger log;
    private final Data data;
    private boolean running;

    public Game(Logger log, Data data) {
        this.log = log;
        this.data = data;
        this.running = false;
    }

    public void start() {
        if (this.running) {
            this.log.warning("Attempted to load a new game, but an instance of the game is already running. " +
                    "Ignoring it");
            return;
        }

        this.running = true;
        int i = 0;

        for (final Player player : this.data.players()) {
            player.teleport(this.data.settings().playerLocations().get(i));
            ++i;
        }
    }

    public void reconnect(Player player) {
        this.data.players().add(player);
    }

    public void disconnect(Player player) {
        this.data.players().remove(player);
    }

    public boolean isPresent(Player player) {
        return this.data.players().contains(player);
    }

    public void broadcast(Component message) {
        this.data.players().forEach(player -> player.sendMessage(message));
    }

}
