package fr.tartur.games.runningegg.game;

import fr.tartur.games.runningegg.Core;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;

public record GameSettings(List<Location> playerLocations, Location spinLocation, int minPlayers) {

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
            playerLocations.add(spawnPoints.getLocation(label));
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
