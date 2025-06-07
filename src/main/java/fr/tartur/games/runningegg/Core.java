package fr.tartur.games.runningegg;

import com.mojang.brigadier.arguments.StringArgumentType;
import fr.tartur.games.runningegg.commands.RunningEggCommands;
import fr.tartur.games.runningegg.game.GameManager;
import fr.tartur.games.runningegg.game.GameSettings;
import fr.tartur.games.runningegg.game.WaitingRoom;
import fr.tartur.games.runningegg.listeners.DelayedEventListener;
import fr.tartur.games.runningegg.listeners.PlayerInvariantListener;
import fr.tartur.games.runningegg.listeners.PlayerStreamListener;
import io.papermc.paper.command.brigadier.Commands;
import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Optional;

public final class Core extends JavaPlugin {

    /**
     * Callback triggered when the plugin needs to load its resources.
     */
    @Override
    public void onEnable() {
        super.saveDefaultConfig();

        final PluginManager manager = super.getServer().getPluginManager();
        final Optional<GameSettings> settings = GameSettings.loadConfiguration(this);
        
        super.getLifecycleManager().registerEventHandler(LifecycleEvents.COMMANDS, event -> {
            final RunningEggCommands commands = new RunningEggCommands(this);
            
            final var spawnLabel = Commands.argument("spawn_label", StringArgumentType.word());
            
            event.registrar().register(Commands.literal("runningegg")
                    .then(Commands.literal("spawn")
                            .then(Commands.literal("set")
                                    .requires(stack -> stack.getSender() instanceof Player)
                                    .then(spawnLabel.executes(commands::setSpawn)))
                            .then(Commands.literal("delete")
                                    .then(spawnLabel.executes(commands::delSpawn)))
                            .then(Commands.literal("teleport")
                                    .requires(stack -> stack.getSender() instanceof Player)
                                    .then(spawnLabel.executes(commands::teleportToSpawn)))
                    )
                    .build());
        });

        if (settings.isEmpty()) {
            getLogger().warning("Could not load game settings written in config.yml.");
        } else {
            final WaitingRoom waitingRoom = new WaitingRoom(this, settings.get());
            final GameManager gameManager = new GameManager(this, waitingRoom);

            manager.registerEvents(new DelayedEventListener(this), this);
            manager.registerEvents(new PlayerInvariantListener(), this);
            manager.registerEvents(gameManager, this);
            manager.registerEvents(new PlayerStreamListener(gameManager), this);
        }
    }

    /**
     * Callback triggered when the plugin needs to stop.
     */
    @Override
    public void onDisable() {
        getLogger().info("Goodbye, my friend! See you very soon! :)");
    }

}
