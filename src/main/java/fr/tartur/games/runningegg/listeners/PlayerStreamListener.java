package fr.tartur.games.runningegg.listeners;

import fr.tartur.games.runningegg.game.Game;
import fr.tartur.games.runningegg.game.GameManager;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.Optional;

public class PlayerStreamListener implements Listener {

    private final GameManager manager;

    public PlayerStreamListener(GameManager manager) {
        this.manager = manager;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        final Player player = event.getPlayer();
        player.setGameMode(GameMode.ADVENTURE);
        player.getInventory().clear();
        player.clearActivePotionEffects();
        player.setLevel(0);
        player.setExp(0);
        final Optional<Game> correspondingGame = this.manager.getGameOfPlayer(player);

        if (correspondingGame.isEmpty()) {
            this.manager.join(player);
        }
    }

    @EventHandler
    public void onPlayerLeave(PlayerQuitEvent event) {
        final Player player = event.getPlayer();
        final Optional<Game> correspondingGame = this.manager.getGameOfPlayer(player);

        if (correspondingGame.isPresent()) {
            final Game game = correspondingGame.get();
            game.disconnect(player);
        } else {
            this.manager.leave(player);
        }
    }

}
