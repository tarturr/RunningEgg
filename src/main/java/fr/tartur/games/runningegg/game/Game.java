package fr.tartur.games.runningegg.game;

import fr.tartur.games.runningegg.Core;
import fr.tartur.games.runningegg.api.events.*;
import io.papermc.paper.event.player.PlayerItemFrameChangeEvent;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.title.Title;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.ItemStack;

import java.util.*;

/**
 * Class representing a whole game, able to manage a whole Running Egg game during its lifetime.
 */
public class Game implements ArrowStopListener, Listener {

    /**
     * Record which groups the game players as a {@link List<Player>}, and also contains the {@link GameSettings}.
     * 
     * @param players The game players.
     * @param settings The game settings.
     */
    public record Data(List<Player> players, GameSettings settings) {}

    /**
     * Enumeration which represents the possible game states.
     */
    public enum State {
        LOADING,
        CHOOSING,
        CHASING,
        WIN
    }

    /**
     * List which stores the {@link HandlerList} of each event listened by the class. Thus, it can be unregistered when
     * the game ends, allowing the garbage collector to remove it from the memory.
     */
    private static final List<HandlerList> REGISTERED_EVENTS = List.of(
            PlayerItemFrameChangeEvent.getHandlerList(),
            PlayerHitsWorldBorderEvent.getHandlerList(),
            EntityHitsWorldBorderEvent.getHandlerList(),
            PlayerHitByEggEvent.getHandlerList(),
            BlockHitByEggEvent.getHandlerList(),
            PlayerMoveEvent.getHandlerList()
    );

    private final Core core;
    private final Data data;
    private final Location middle;
    private final FrameManager frame;
    private final GamePlayers players;
    private State state;

    /**
     * Class constructor, which sets the game state as {@link State#LOADING} and loads every resource it needs depending
     * on the provided {@link Data}.
     * 
     * @param core The main class of the plugin.
     * @param data The game data.
     */
    public Game(Core core, Data data) {
        this.core = core;
        this.data = data;
        this.middle = data.settings().spinLocation();
        this.frame = new FrameManager(core, this, this.middle);
        this.players = new GamePlayers(data);
        
        this.state = State.LOADING;
    }

    /**
     * Starts the game, which also creates the world border and starts the game loop.
     */
    public void start() {
        if (!this.isRunning()) {
            final WorldBorder border = this.middle.getWorld().getWorldBorder();
            border.setCenter(this.middle);
            border.setSize(50);
            this.loop();
        }
    }

    /**
     * Main game loop, which sets the game's state to {@link State#CHOOSING}, picks a random player and defines it as
     * the game {@link GameRole#HUNTER}, and spins the arrow (which will, at some point, point to the predefined
     * hunter).
     */
    private void loop() {
        if (this.state == State.CHOOSING) {
            return;
        }
        
        this.reset();
        
        final int hunterIndex = this.players.defineRandomHunter();
        
        for (final Player player : this.players.getPlaying()) {
            player.showTitle(Title.title(
                    Component.text("Sélection du chasseur...", NamedTextColor.GOLD),
                    Component.text("Apprêtez-vous à courir !", NamedTextColor.GREEN)
            ));
        }
        
        this.frame.spinArrow(hunterIndex);
    }

    /**
     * Clears each player's potion effects and puts it role to {@link GameRole#WAITING}.
     */
    private void reset() {
        this.state = State.CHOOSING;
        this.frame.setArrow();
        this.players.reset();
    }

    /**
     * Properly ends the game, settings its state to {@link State#WIN}, with the given {@link Player} considered as the
     * winner.
     *
     * @param winner The game winner.
     */
    private void stop(Player winner) {
        if (this.state != State.WIN) {
            final WorldBorder border = this.middle.getWorld().getWorldBorder();
            border.setSize(border.getMaxSize());
            
            this.state = State.WIN;
            this.players.win(winner);

            Bukkit.getScheduler().scheduleSyncDelayedTask(this.core,
                    () -> new GameEndEvent(this).callEvent(), 5 * 20L);
        }
    }

    /**
     * Callback triggered when the arrow has stopped spinning. Thus, the hunter is announced with some visual and sounds
     * effects, the arrow is replaced by an egg, and the game's state is updated to {@link State#CHASING}.
     */
    @Override
    public void onArrowStop() {
        this.state = State.CHASING;
        this.frame.setEgg();
        this.players.alert();
    }

    /**
     * Event callback triggered when the player tries to steal the item from the item frame, which is cancelled either
     * if the player is not the {@link GameRole#HUNTER}, or if the game's state is not {@link State#CHASING} or if the
     * event is not of type {@link PlayerItemFrameChangeEvent.ItemFrameChangeAction#REMOVE}.
     * 
     * @param event The event.
     */
    @EventHandler
    public void onPlayerClickAtItemFrame(PlayerItemFrameChangeEvent event) {
        event.setCancelled(true);
        final Player player = event.getPlayer();

        if (this.state == State.CHASING
                && this.players.hasRole(player, GameRole.HUNTER)
                && event.getAction() == PlayerItemFrameChangeEvent.ItemFrameChangeAction.ROTATE) {
            this.frame.empty();
            player.getInventory().addItem(ItemStack.of(Material.EGG));
            this.middle.getWorld().strikeLightningEffect(this.middle);
        } else {
            player.sendMessage(Component.text("Vous ne pouvez pas faire cela.", NamedTextColor.DARK_RED));
        }
    }

    /**
     * Event callback triggered when a player playing as a {@link GameRole#PRAY} hits the world border.
     * 
     * @param event The event.
     */
    @EventHandler
    public void onPlayerHitsWorldBorder(PlayerHitsWorldBorderEvent event) {
        final Player player = event.getPlayer();
        
        if (this.players.hasRole(player, GameRole.PRAY)) {
            this.players.escape(player);

            if (this.players.getPrays().isEmpty()) {
                for (final Player playing : this.getPlayers().getPlaying()) {
                    playing.showTitle(Title.title(
                            Component.text("PROIES ÉCHAPPÉES", NamedTextColor.DARK_AQUA),
                            Component.text("Toutes les proies se sont enfuies !", NamedTextColor.LIGHT_PURPLE)
                    ));
                }
                
                Bukkit.getScheduler().scheduleSyncDelayedTask(this.core, this::loop, 20L * 5L);
            }
        }
    }

    /**
     * Event callback triggered when a thrown egg hits the {@link WorldBorder}.
     * 
     * @param event The event.
     */
    @EventHandler
    public void onEggHitsWorldBorder(EntityHitsWorldBorderEvent event) {
        if (!(event.getEntity() instanceof final Egg egg)) {
            return;
        }
        
        egg.remove();
        this.loop();
    }

    /**
     * Event callback triggered when an {@link Egg} hits a {@link Player}.
     *
     * @param event The event.
     */
    @EventHandler
    public void onPlayerHitByEgg(PlayerHitByEggEvent event) {
        event.setCancelled(true);

        final Player player = event.getHitEntity();

        if (this.players.hasRole(player, GameRole.HUNTER)) {
            player.sendMessage(Component.text("Vous ne pouvez pas vous tirer dessus !",
                    NamedTextColor.DARK_RED));
            player.getInventory().addItem(ItemStack.of(Material.EGG));

            return;
        } else {
            this.players.lose(player);
        }

        final List<Player> players = this.players.getPlaying();

        if (players.size() == 1) {
            this.stop(players.getFirst());
        } else {
            this.loop();
        }
    }

    /**
     * Event callback triggered when an {@link Egg} hits a {@link Block}.
     *
     * @param event The event.
     */
    @EventHandler
    public void onBlockHitByEgg(BlockHitByEggEvent event) {
        event.setCancelled(true);
        this.loop();
    }

    /**
     * Event callback triggered when a player tries to move while the game state is set to {@link State#CHOOSING}.
     *
     * @param event The event.
     */
    @EventHandler
    public void onMove(PlayerMoveEvent event) {
        if (this.state == State.CHOOSING) {
            event.setCancelled(true);
        }
    }

    /**
     * Sets the provided {@link Player} as a {@link GameRole#SPECTATOR}.
     * 
     * @param player The disconnecting player.
     */
    public void disconnect(Player player) {
        if (this.players.hasRole(player, GameRole.HUNTER)) {
            this.players.setSpectator(player);
            this.broadcast(Component.text("Le chasseur a quitté la partie !", NamedTextColor.RED));
            this.loop();
        } else {
            this.players.setSpectator(player);
        }
    }

    /**
     * Gets the players in the current game with a {@link GamePlayers} instance.
     * 
     * @return The said player list.
     */
    public GamePlayers getPlayers() {
        return players;
    }

    /**
     * Checks if the provided {@link Player} is in the game.
     * 
     * @param player The player.
     * @return {@code true} if the player is in the game, {@code false} otherwise.
     */
    public boolean isPresent(Player player) {
        return this.data.players().contains(player);
    }

    /**
     * Sends the provided {@link Component} to every player of the game.
     * 
     * @param message The message.
     */
    public void broadcast(Component message) {
        for (final Player player : this.players.getAll()) {
            player.sendMessage(message);
        }
    }

    /**
     * Checks if the game is running.
     * 
     * @return {@code true} if the state is not {@link State#LOADING}, {@code false} otherwise.
     */
    public boolean isRunning() {
        return this.state != State.LOADING;
    }

    /**
     * Unregisters the class from every event it listens to.
     */
    public void unregisterEvents() {
        REGISTERED_EVENTS.forEach(handler -> handler.unregister(this));
    }

    /**
     * Gets the class {@link Game.Data} instance.
     * 
     * @return The {@code Game.Data} instance.
     */
    public Data getData() {
        return data;
    }
}
