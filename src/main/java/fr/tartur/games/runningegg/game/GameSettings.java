package fr.tartur.games.runningegg.game;

import fr.tartur.games.runningegg.Core;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;

/**
 * Records which stores game settings, oftenly loaded from the plugin's {@code config.yml} file.
 *
 * @param playerLocations Each spawn point where a player can appear.
 * @param spinLocation The spawn point of the {@link org.bukkit.entity.ItemFrame} containing the spinning arrow or the
 *                     throwable egg.
 * @param minPlayers The minimum amount of players needed to start a game.
 *
 * @see GameSettings#loadConfiguration(Core)
 */
public record GameSettings(List<Location> playerLocations, Location spinLocation, int minPlayers) {

    /**
     * Loads a new {@code GameSettings} instance wrapped in a {@link Optional} if the plugin's {@code config.yml} file
     * was correctly formed, or {@link Optional#empty()} if any of the {@link ConfigurationSection} is missing or empty.
     *
     * @param core The main plugin instance used to read its {@code config.yml} file and log messages in the console.
     * @return An {@code Optional} wrapping the configuration-loaded {@code GameSettings} instance if the configuration
     * was correctly formed, or {@code Optional.empty()} otherwise.
     */
    public static Optional<GameSettings> loadConfiguration(Core core) {
        final FileConfiguration configuration = core.getConfig();
        final Logger log = core.getLogger();

        final ConfigurationSection spawnPoints = configuration.getConfigurationSection("spawn_points");

        if (spawnPoints == null) {
            log.severe("Could not find the 'spawn_points' section in config.yml.");
            return Optional.empty();
        }

        final List<Location> playerLocations = new ArrayList<>();

        for (final String label : spawnPoints.getKeys(false)) {
            if (!label.equals("spin")) {
                playerLocations.add(spawnPoints.getLocation(label));
            }
        }

        if (playerLocations.isEmpty()) {
            log.severe("No player spawn point has been set in config.yml. Cannot start any game.");
            return Optional.empty();
        }

        final Location spinLocation = spawnPoints.getLocation("spin");

        if (spinLocation == null) {
            log.severe("No spawn point has been provided for the spinning arrow in config.yml. Cannot " +
                    "start any game.");
            return Optional.empty();
        }

        int minPlayers = configuration.getInt("min_players");

        if (minPlayers < 2) {
            log.warning("Minimum players amount should at least be 2. Setting this value by default.");
            minPlayers = 2;
        }

        log.info("Plugin configuration was successfully loaded!");
        return Optional.of(new GameSettings(playerLocations, spinLocation, minPlayers));
    }

}
