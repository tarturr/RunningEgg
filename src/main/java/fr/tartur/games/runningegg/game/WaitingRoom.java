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

public class WaitingRoom {

    private final List<Player> players;
    private final Core core;
    private final GameSettings settings;

    private int task;

    public WaitingRoom(Core core, GameSettings settings) {
        this.core = core;
        this.settings = settings;
        this.players = new ArrayList<>();
        this.task = -1;
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

    private void stopScheduler() {
        if (this.task != -1) {
            Bukkit.getScheduler().cancelTask(this.task);
            this.task = -1;
        }
    }

    private boolean isStartable() {
        return this.players.size() >= this.settings.minPlayers() && task == -1;
    }

    private void reset() {
        this.stopScheduler();
        this.players.clear();
    }

}
