package fr.tartur.games.runningegg.game;

import fr.tartur.games.runningegg.Core;
import fr.tartur.games.runningegg.api.events.GameStartEvent;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.title.Title;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

/**
 * Class which handles the players' entries and exits, and decides whether a new game can start.
 */
public class WaitingRoom {

    private final List<Player> players;
    private final Core core;
    private final GameSettings settings;

    private int task;

    /**
     * Class constructor which needs the {@link Core} plugin instance to start a timer to alert player when the game
     * will start. At the end, a {@link GameStartEvent} will be fired with the given {@link GameSettings}.
     *
     * @param core The main plugin instance.
     * @param settings The game settings.
     */
    public WaitingRoom(Core core, GameSettings settings) {
        this.core = core;
        this.settings = settings;
        this.players = new ArrayList<>();
        this.task = -1;
    }

    /**
     * Adds the provided {@link Player} to the list of waiting players.
     *
     * @param player The new player.
     */
    public void join(Player player) {
        this.players.add(player);

        if (this.isStartable()) {
            this.startScheduler();
        }
    }

    /**
     * Removes the provided {@link Player} from the list of waiting players.
     *
     * @param player The leaving player.
     */
    public void leave(Player player) {
        this.players.remove(player);

        if (!this.isStartable()) {
            this.stopScheduler();
        }
    }

    /**
     * Starts the scheduler which will alert every player that a new game will start soon.
     */
    private void startScheduler() {
        this.task = Bukkit.getScheduler().scheduleSyncRepeatingTask(this.core, new Runnable() {
            private int timer = 30;
            
            @Override
            public void run() {
                for (final Player player : players) {
                    player.setExp((float) timer / 30f);
                    player.setLevel(timer);

                    switch (timer) {
                        case 30, 20, 15, 10, 5, 4, 3, 2, 1 -> {
                            player.showTitle(Title.title(
                                    Component.text(this.timer + "s", NamedTextColor.GOLD),
                                    Component.text("La partie va commencer !", NamedTextColor.RED)
                            ));

                            player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1.5f, 1f);
                        }
                    }
                }

                if (timer == 0) {
                    new GameStartEvent(players, settings).callEvent();
                    reset();
                }
                
                --timer;
            }
        }, 0L, 20L);
    }

    /**
     * Stops the running scheduler, if it exists.
     */
    private void stopScheduler() {
        if (this.task != -1) {
            Bukkit.getScheduler().cancelTask(this.task);
            this.task = -1;
        }
    }

    /**
     * Checks whether a new game can be started.
     *
     * @return {@code true} if no running task was already running and if the amount of waiting players is greater or
     * equal to the minimum amount provided in the class {@link GameSettings}, or {@code false} otherwise.
     */
    private boolean isStartable() {
        return this.players.size() >= this.settings.minPlayers() && task == -1;
    }

    /**
     * Stops the working scheduler and empties the list of waiting players.
     */
    private void reset() {
        this.stopScheduler();
        this.players.clear();
    }

}
