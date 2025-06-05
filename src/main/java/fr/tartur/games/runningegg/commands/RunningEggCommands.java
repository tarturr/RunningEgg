package fr.tartur.games.runningegg.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.context.CommandContext;
import fr.tartur.games.runningegg.Core;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

/**
 * Class which contains all (sub)command executions of the root {@code /runningegg} command.
 */
public class RunningEggCommands {

    private final Core core;
    private final FileConfiguration config;

    /**
     * Class constructor, which needs a {@link Core} instance to save (or remove) positions to (or from) the plugin's
     * {@code config.yml} file.
     *
     * @param core The main plugin instance.
     */
    public RunningEggCommands(Core core) {
        this.core = core;
        this.config = core.getConfig();
    }

    /**
     * {@code /runningegg spawn set <spawn_label>} command executor, which defines a new spawn point in the plugin
     * {@code config.yml} at section {@code spawn_point.spawn_label}, whene {@code spawn_label} is the given spawn point
     * name.
     *
     * @param context The command context.
     * @return {@link Command#SINGLE_SUCCESS} every time the command was successfully executed.
     */
    public int setSpawn(CommandContext<CommandSourceStack> context) {
        final String label = context.getArgument("spawn_label", String.class);
        final String path = "spawn_points." + label;
        final Player player = (Player) context.getSource().getSender();
        final Location location = player.getLocation();
        
        if (this.config.getLocation(path) != null) {
            player.sendMessage(Component.text(("Le point d'apparition '%s' a déjà été défini. Tapez '/runningegg " +
                            "spawn teleport %s' pour vous y rendre.").formatted(label, label),
                    NamedTextColor.RED));

            return Command.SINGLE_SUCCESS;
        }
        
        this.config.set("spawn_points." + label, location);
        this.core.saveConfig();
        player.sendMessage(Component.text("Le point d'apparition '%s' a bien été défini !".formatted(label),
                NamedTextColor.GREEN));
        
        return Command.SINGLE_SUCCESS;
    }

    /**
     * {@code /runningegg spawn delete <spawn_label>} command executor, which deletes the existing spawn point from the
     * plugin {@code config.yml} file at section {@code spawn_points.spawn_label}, where {@code spawn_label} is the
     * given spawn point name.
     *
     * @param context The command context.
     * @return {@link Command#SINGLE_SUCCESS} every time the command was successfully executed.
     */
    public int delSpawn(CommandContext<CommandSourceStack> context) {
        final String label = context.getArgument("spawn_label", String.class);
        final String path = "spawn_points." + label;
        final CommandSender sender = context.getSource().getSender();
        
        if (this.config.getLocation(path) == null) {
            sender.sendMessage(Component.text("Le point d'apparition '%s' n'existe pas.".formatted(label),
                    NamedTextColor.RED));

            return Command.SINGLE_SUCCESS;
        }

        this.config.set("spawn_points." + label, null);
        this.core.saveConfig();
        sender.sendMessage(Component.text("Le point d'apparition '%s' a bien été supprimé !".formatted(label),
                NamedTextColor.GREEN));
        
        return Command.SINGLE_SUCCESS;
    }

    /**
     * {@code /runningegg spawn set <spawn_label>} command executor, which teleports the {@link Player} who executes it
     * at the location stored in the plugin {@code config.yml} file, at section {@code spawn_points.spawn_label}, where
     * {@code spawn_label} is the given spawn point name.
     *
     * @param context The command context.
     * @return {@link Command#SINGLE_SUCCESS} every time the command was successfully executed.
     */
    public int teleportToSpawn(CommandContext<CommandSourceStack> context) {
        final String label = context.getArgument("spawn_label", String.class);
        final String path = "spawn_points." + label;
        final Player player = (Player) context.getSource().getSender();
        final Location location = this.config.getLocation(path);

        if (location == null) {
            player.sendMessage(Component.text("Le point d'apparition '%s' n'existe pas.".formatted(label),
                    NamedTextColor.RED));

            return Command.SINGLE_SUCCESS;
        }
        
        player.teleport(location);
        player.sendMessage(Component.text("Téléportation au point d'apparition '%s'.".formatted(label),
                NamedTextColor.GREEN));

        return Command.SINGLE_SUCCESS;
    }
}
