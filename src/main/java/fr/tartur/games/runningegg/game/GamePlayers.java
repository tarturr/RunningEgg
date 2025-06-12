package fr.tartur.games.runningegg.game;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.title.Title;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.time.Duration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

/**
 * Class which handles every player interaction during the game progress.
 */
public class GamePlayers {

    private final Map<Player, GameRole> roles;
    private final Random random;
    private final Location middle;
    private final List<Location> spawnPoints;
    private Player hunter;

    /**
     * Class constructor, which laods many resources depending on the provided {@link Game.Data} instance.
     *
     * @param data The game data.
     */
    public GamePlayers(Game.Data data) {
        this.roles = new HashMap<>();
        this.random = new Random();
        this.middle = data.settings().spinLocation();
        this.spawnPoints = data.settings().playerLocations();
        
        for (final Player player : data.players()) {
            this.roles.put(player, GameRole.WAITING);
        }
    }
    
    /**
     * Defines a randomly-chosen new {@link GameRole#HUNTER}, putting every other player as {@link GameRole#PRAY}.
     */
    public int defineRandomHunter() {
        final int index = this.pickRandomPlayer();
        this.roles.put(this.hunter, GameRole.HUNTER);

        for (final Player pray : this.getPlaying()) {
            if (pray != this.hunter) {
                this.roles.put(pray, GameRole.PRAY);
            }
        }
        
        return index;
    }

    /**
     * Teleports every player to different spawn points according to the {@link Game.Data}, heals & feeds them, clears
     * their potion effects & inventory and sets their game mode to {@link GameMode#ADVENTURE}.
     */
    public void reset() {
        final List<Player> players = this.getPlaying();

        for (int i = 0; i < players.size(); ++i) {
            final Player player = players.get(i);
            final Location location = this.spawnPoints.get(i);

            player.setHealth(20);
            player.setFoodLevel(20);
            player.clearActivePotionEffects();
            player.getInventory().clear();
            player.setGameMode(GameMode.ADVENTURE);
            this.roles.put(player, GameRole.WAITING);

            player.teleport(location);
        }
    }

    /**
     * Alerts every player whether they are a {@link GameRole#HUNTER} or {@link GameRole#PRAY} using {@link Title} and
     * {@link Sound} effects. Then, a {@link PotionEffectType#SPEED} of level {@code 1} with
     * {@link PotionEffect#INFINITE_DURATION} is applied to all players with the role {@link GameRole#PRAY}.
     */
    public void alert() {
        final PotionEffect speed = new PotionEffect(PotionEffectType.SPEED, PotionEffect.INFINITE_DURATION, 1);
        final Title.Times duration = Title.Times.times(
                Duration.ofMillis(250L),
                Duration.ofMillis(1500L),
                Duration.ofMillis(250L)
        );

        this.playSound(this.hunter, Sound.ENTITY_ENDER_DRAGON_GROWL);
        this.hunter.showTitle(Title.title(
                Component.text("CHASSEUR", NamedTextColor.RED),
                Component.text("Attrapez l'oeuf et tirez sur vos proies !", NamedTextColor.DARK_RED),
                duration
        ));
        
        for (final Player pray : this.getPrays()) {
            this.playSound(pray, Sound.ENTITY_CAT_AMBIENT);
            
            pray.showTitle(Title.title(
                    Component.text("PROIE", NamedTextColor.AQUA),
                    Component.text("Fuyez sans vous faire toucher !", NamedTextColor.DARK_AQUA),
                    duration
            ));

            pray.addPotionEffect(speed);
        }
    }
    
    /**
     * Marks the player as escaped, which defines its role as {@link GameRole#WAITING} and sets its game mode to
     * {@link GameMode#SPECTATOR}.
     *
     * @param player The escaped player.
     */
    public void escape(Player player) {
        this.roles.put(player, GameRole.WAITING);
        
        player.teleport(this.middle);
        player.setGameMode(GameMode.SPECTATOR);
        this.playSound(player, Sound.BLOCK_NOTE_BLOCK_PLING);
        player.showTitle(Title.title(
                Component.text("OUF !", NamedTextColor.GREEN),
                Component.text("Vous vous êtes enfui... pour le moment.", NamedTextColor.GRAY)
        ));
    }

    /**
     * Alerts the provided player that he losed the game using {@link Title} and {@link Sound} effects.
     *
     * @param player The losing player.
     */
    public void lose(Player player) {
        this.setSpectator(player);
        
        player.showTitle(Title.title(
                Component.text("PERDU", NamedTextColor.RED),
                Component.text("Vous avez été touché par le chasseur !", NamedTextColor.DARK_RED)
        ));
        player.sendMessage(Component.text("Vous avez perdu, retentez votre chance la prochaine " +
                "fois ! Vous pouvez également observer la partie.", NamedTextColor.GOLD));
        this.playSound(player, Sound.ENTITY_BAT_DEATH);
    }

    /**
     * Alerts the provided player that he won the game and every other player that the game has reached its end, using
     * {@link Title} and {@link Sound} effects.
     *
     * @param winner The winning player.
     */
    public void win(Player winner) {
        winner.setGameMode(GameMode.ADVENTURE);
        this.playSound(winner, Sound.ENTITY_PLAYER_LEVELUP);
        winner.showTitle(Title.title(
                Component.text("VICTOIRE", NamedTextColor.GOLD, TextDecoration.BOLD),
                Component.text("Vous êtes le meilleur !", NamedTextColor.GREEN)
        ));
        winner.sendMessage(Component.text("Félicitations, vous avez été le meilleur joueur de la " +
                        "partie ! Rejouez tant que vous le souhaitez pour, une fois de plus, montrer votre talent !",
                NamedTextColor.GREEN));
        
        for (final Player gamePlayer : this.getAll()) {
            gamePlayer.teleport(this.middle);
            this.playSound(gamePlayer, Sound.ENTITY_WITHER_DEATH);

            if (gamePlayer != winner) {
                this.setSpectator(gamePlayer);
                gamePlayer.showTitle(Title.title(
                        MiniMessage.miniMessage().deserialize("<rainbow>FIN DE PARTIE")
                                .decorate(TextDecoration.BOLD), 
                        Component.text(winner.getName(), NamedTextColor.RED)
                                .append(Component.text(" a gagné !", NamedTextColor.AQUA))
                ));
            }
        }
    }

    /**
     * Sets the given player as {@link GameRole#SPECTATOR}, changing its {@link GameMode}.
     *
     * @param player The player.
     */
    public void setSpectator(Player player) {
        player.setGameMode(GameMode.SPECTATOR);
        this.roles.put(player, GameRole.SPECTATOR);
    }
    
    /**
     * Builds a {@link List<Player>} with the role {@link GameRole#PRAY}.
     *
     * @return A list of players matching the pray role.
     */
    public List<Player> getPrays() {
        return this.getAll().stream()
                .filter(player -> this.hasRole(player, GameRole.PRAY))
                .toList();
    }

    /**
     * Builds a {@link List<Player>} composed by players which DO NOT have the role {@link GameRole#SPECTATOR}.
     *
     * @return A list of players which are not spectators.
     */
    public List<Player> getPlaying() {
        return this.getAll().stream()
                .filter(player -> !this.hasRole(player, GameRole.SPECTATOR))
                .toList();
    }

    /**
     * Returns every {@link Player} of the game, without {@link GameRole} distinction.
     *
     * @return A {@code List<Player>} of every player watching or participating in the game.
     */
    public List<Player> getAll() {
        return this.roles.keySet().stream().toList();
    }

    /**
     * Checks if the given {@link Player} has the given {@link GameRole}.
     *
     * @param player The player.
     * @param role The role to check.
     * @return {@code true} if the player actually has the role, {@code false} otherwise.
     */
    public boolean hasRole(Player player, GameRole role) {
        return this.roles.get(player) == role;
    }

    /**
     * Picks a random player from the {@code List<Player>} returned by the {@link this#getPlaying()} method.
     *
     * @return A randomly-chosen player.
     */
    private int pickRandomPlayer() {
        final List<Player> players = this.getPlaying();
        final int index = this.random.nextInt(players.size());
        
        this.hunter = players.get(index);
        return index;
    }

    /**
     * Plays the provided {@link Sound} to the given {@link Player} at volume {@code 1} and pitch {@code 1}.
     *
     * @param player The player to play the sound to.
     * @param sound The sound to play.
     */
    private void playSound(Player player, Sound sound) {
        player.playSound(player.getLocation(), sound, 1f, 1f);
    }
    
}
