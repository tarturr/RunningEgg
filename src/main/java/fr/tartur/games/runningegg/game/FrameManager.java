package fr.tartur.games.runningegg.game;

import fr.tartur.games.runningegg.Core;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Rotation;
import org.bukkit.entity.ItemFrame;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitTask;

import java.util.Iterator;

/**
 * Class used to handle every interaction the {@link ItemFrame} at the given {@link Location} on class construction.
 *
 * @see FrameManager#FrameManager(Core, ArrowStopListener, Location)
 */
public class FrameManager implements ArrowStopListener {

    private final Core core;
    private final ArrowStopListener listener;
    private final Location location;
    private BukkitTask rotator;

    /**
     * Class constructor, which needs the {@link Core} plugin instance to start a {@link BukkitTask}, a {@link Location}
     * where the {@link ItemFrame} is, and a {@link ArrowStopListener} to trigger when the arrow stops spinning.
     *
     * @param core The main plugin instance.
     * @param listener The listener to trigger on arrow stop.
     * @param location The location of the {@code ItemFrame}.
     */
    public FrameManager(Core core, ArrowStopListener listener, Location location) {
        this.core = core;
        this.listener = listener;
        this.location = location;
        this.rotator = null;
    }

    /**
     * Sets an {@link ItemStack} of type {@link Material#ARROW} in the class {@link ItemFrame}.
     */
    public void setArrow() {
        final ItemFrame frame = this.getItemFrame();
        frame.setRotation(Rotation.NONE);
        frame.setItem(ItemStack.of(Material.ARROW));
    }

    /**
     * Sets an {@link ItemStack} of type {@link Material#EGG} in the class {@link ItemFrame}.
     */
    public void setEgg() {
        final ItemFrame frame = this.getItemFrame();
        frame.setItem(ItemStack.of(Material.EGG));
    }

    /**
     * Empties the class {@link ItemFrame}.
     */
    public void empty() {
        final ItemFrame frame = this.getItemFrame();
        frame.setItem(null);
    }

    /**
     * Starts the arrow spinning animation.
     *
     * @param target The direction index where the arrow has to point at when it stops spinning.
     * @see ArrowRotator
     */
    public void spinArrow(int target) {
        this.rotator = Bukkit.getScheduler().runTaskTimer(this.core,
                new ArrowRotator(this, this.getItemFrame(), target), 0L, 2L);
    }

    /**
     * Method called by the {@link ArrowRotator} instance when the arrow stops spinning. Thus, the class
     * {@link BukkitTask} will be cancelled, set to null and the class {@link ArrowStopListener} will then be triggered.
     */
    @Override
    public void onArrowStop() {
        this.rotator.cancel();
        this.rotator = null;
        
        this.listener.onArrowStop();
    }

    /**
     * The {@link ItemFrame} at the given class {@link Location}.
     *
     * @return The said {@code ItemFrame}.
     */
    private ItemFrame getItemFrame() {
        final Iterator<ItemFrame> iterator = this.location
                .getNearbyEntitiesByType(ItemFrame.class, 1d)
                .iterator();

        if (iterator.hasNext()) {
            return iterator.next();
        } else {
            return this.location.getWorld().spawn(this.location, ItemFrame.class);
        }
    }
    
}
