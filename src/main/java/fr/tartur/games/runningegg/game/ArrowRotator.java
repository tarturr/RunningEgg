package fr.tartur.games.runningegg.game;

import org.bukkit.Location;
import org.bukkit.Rotation;
import org.bukkit.Sound;
import org.bukkit.entity.ItemFrame;

import java.util.Map;

public class ArrowRotator implements Runnable {
    private static final Map<Integer, Rotation> ROTATION_MAP = Map.of(
            0,   Rotation.NONE,
            45,  Rotation.CLOCKWISE_45,
            90,  Rotation.CLOCKWISE,
            135, Rotation.CLOCKWISE_135,
            180, Rotation.FLIPPED,
            225, Rotation.FLIPPED_45,
            270, Rotation.COUNTER_CLOCKWISE,
            315, Rotation.COUNTER_CLOCKWISE_45
    );

    private final ArrowStopListener listener;
    private final ItemFrame frame;
    private final int target;
    private int itemPosition;
    private int rotations;

    public ArrowRotator(ArrowStopListener listener, ItemFrame frame, int target) {
        this.listener = listener;
        this.frame = frame;
        this.target = target;
        this.itemPosition = 0;
        this.rotations = 0;
    }

    @Override
    public void run() {
        final int spins = this.rotations / 8;
        final int angle = this.itemPosition * 45 % 360;
        
        if (spins < 3) {
            this.rotate(angle);
        } else if (spins < 4 && this.rotations % 2 == 0) {
            this.rotate(angle);
        } else if (spins >= 4 && this.rotations % 4 == 0) {
            this.rotate(angle);
            
            final int index = this.itemPosition % 8;
            
            if (index == target) {
                this.listener.onArrowStop();
            }
        }
        
        ++this.rotations;
    }
    
    private void rotate(int angle) {
        final Location location = this.frame.getLocation();
        this.frame.setRotation(ROTATION_MAP.get(angle));
        location.getWorld().playSound(location, Sound.UI_BUTTON_CLICK, 3f, 3f);
        ++this.itemPosition;
    }
}
