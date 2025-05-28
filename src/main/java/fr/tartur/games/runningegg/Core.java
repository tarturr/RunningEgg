package fr.tartur.games.runningegg;

import fr.tartur.games.runningegg.game.GameManager;
import fr.tartur.games.runningegg.game.GameSettings;
import fr.tartur.games.runningegg.game.WaitingRoom;
import fr.tartur.games.runningegg.listeners.PlayerStreamListener;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Optional;

public final class Core extends JavaPlugin {

    @Override
    public void onEnable() {
        super.saveDefaultConfig();

        final PluginManager manager = super.getServer().getPluginManager();
        final Optional<GameSettings> settings = GameSettings.loadConfiguration(this);

        if (settings.isEmpty()) {
            getLogger().severe("Could not load game settings written in config.yml. Aborting...");
            manager.disablePlugin(this);
            return;
        }

        final WaitingRoom waitingRoom = new WaitingRoom(this, settings.get());
        final GameManager gameManager = new GameManager(super.getLogger(), waitingRoom);

        manager.registerEvents(gameManager, this);
        manager.registerEvents(new PlayerStreamListener(gameManager), this);
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

}
