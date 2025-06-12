package fr.tartur.games.runningegg.listeners;

import fr.tartur.games.runningegg.game.GameManager;
import org.bukkit.GameMode;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

/**
 * Class which handles players' entries and exits, interacting with a {@link GameManager} given in the class
 * constructor.
 *
 * @see PlayerStreamListener#PlayerStreamListener(GameManager)
 */
public class PlayerStreamListener implements Listener {

    private final GameManager manager;

    /**
     * Class constructor, which requires a {@link GameManager} instance to share the players' entries and exits.
     *
     * @param manager The said manager.
     */
    public PlayerStreamListener(GameManager manager) {
        this.manager = manager;
    }

    /**
     * Event callback triggered when a player joins the server. Thus, its game mode is set to
     * {@link GameMode#ADVENTURE}, its inventory and potion effects are cleared, and experience level is set to 0. If
     * the player has suddendly leaved a recent game, it automatically will be re-logged in.
     *
     * @param event The event.
     */
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        this.manager.join(event.getPlayer());
    }

    /**
     * Event callback triggered when a player leaves the server. If it was in a running game, the said game will be
     * impacted as it should be. Otherwise, the player is just removed from the waiting room.
     *
     * @param event The event.
     */
    @EventHandler
    public void onPlayerLeave(PlayerQuitEvent event) {
        this.manager.leave(event.getPlayer());
    }

}
