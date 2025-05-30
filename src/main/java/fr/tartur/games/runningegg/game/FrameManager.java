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

public class FrameManager implements ArrowStopListener {

    private final Core core;
    private final ArrowStopListener listener;
    private final Location location;
    private BukkitTask rotator;

    public FrameManager(Core core, ArrowStopListener listener, Location location) {
        this.core = core;
        this.listener = listener;
        this.location = location;
        this.rotator = null;
    }

    public void setArrow() {
        final ItemFrame frame = this.getItemFrame();
        frame.setRotation(Rotation.NONE);
        frame.setItem(ItemStack.of(Material.ARROW));
    }

    public void setEgg() {
        final ItemFrame frame = this.getItemFrame();
        frame.setItem(ItemStack.of(Material.EGG));
    }
    
    public void empty() {
        final ItemFrame frame = this.getItemFrame();
        frame.setItem(null);
    }
    
    public void spinArrow(int target) {
        this.rotator = Bukkit.getScheduler().runTaskTimer(this.core,
                new ArrowRotator(this, this.getItemFrame(), target), 0L, 2L);
    }

    @Override
    public void onArrowStop() {
        this.rotator.cancel();
        this.rotator = null;
        
        this.listener.onArrowStop();
    }
    
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
