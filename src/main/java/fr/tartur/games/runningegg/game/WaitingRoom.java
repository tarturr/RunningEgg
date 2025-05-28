package fr.tartur.games.runningegg.game;

import fr.tartur.games.runningegg.Core;
import fr.tartur.games.runningegg.api.events.GameStartEvent;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.title.Title;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

import java.util.HashSet;
import java.util.Set;

public class WaitingRoom {

    private final Set<Player> players;
    private final Core core;
    private final GameSettings settings;

    private BukkitTask task;

    public WaitingRoom(Core core, GameSettings settings) {
        this.core = core;
        this.settings = settings;
        this.players = new HashSet<>();
    }

    public void join(Player player) {
        this.players.add(player);

        if (this.isStartable()) {
            this.startScheduler();
        }
    }

    public void leave(Player player) {
        this.players.remove(player);

        if (!this.isStartable()) {
            this.stopScheduler();
        }
    }

    private void startScheduler() {
        this.task = Bukkit.getScheduler().runTaskTimer(this.core, new Runnable() {
            private int timer = 30;

            @Override
            public void run() {
                switch (timer) {
                    case 30, 20, 15, 10, 5, 4, 3, 2, 1 -> {
                        for (final Player player : players) {
                            player.showTitle(Title.title(
                                    Component.text(this.timer + "S", NamedTextColor.GOLD),
                                    Component.text("La partie va commencer !", NamedTextColor.RED)
                            ));

                            player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 3f, 3f);
                        }
                    }

                    case 0 -> {
                        new GameStartEvent(players, settings);
                        reset();
                    }
                }

                --timer;
            }
        }, 0L, 20L);
    }

    private void stopScheduler() {
        this.task.cancel();
        this.task = null;
    }

    private boolean isStartable() {
        return this.players.size() >= this.settings.minPlayers() && task == null;
    }

    private void reset() {
        this.players.clear();
        this.task.cancel();
        this.task = null;
    }

}
