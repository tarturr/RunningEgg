package fr.tartur.games.runningegg.game;

import org.bukkit.Location;
import org.bukkit.Rotation;
import org.bukkit.Sound;
import org.bukkit.entity.ItemFrame;

import java.util.Map;

/**
 * Class which handles the arrow spinning animation and triggers its registered listener when the arrow stops.
 *
 * @see ArrowStopListener
 * @see ArrowRotator#ArrowRotator(ArrowStopListener, ItemFrame, int)
 */
public class ArrowRotator implements Runnable {

    /**
     * Map associating every degree with a value in {@code [0, 360[} with a step of {@code 45} to its corresponding
     * {@link Rotation}.
     */
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

    /**
     * Class constructor, which needs an {@link  ItemFrame} instance for the arrow animation and an index at which the
     * arrow will be pointing to when it stops turning. Thus, the provided {@link ArrowStopListener} will be triggered.
     *
     * @param listener The listener which will be triggered when the arrow stops spinning.
     * @param frame The frame containing the arrow.
     * @param target The spot at which the arrow will point at when it stops spinning.
     */
    public ArrowRotator(ArrowStopListener listener, ItemFrame frame, int target) {
        this.listener = listener;
        this.frame = frame;
        this.target = target;
        this.itemPosition = 0;
        this.rotations = 0;
    }

    /**
     * The repetitive animation task, which progressively slows down until it points the target provided on class
     * instantation.
     *
     * @see ArrowRotator#ArrowRotator(ArrowStopListener, ItemFrame, int)
     */
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

    /**
     * Rotates the arrow of the class {@link ItemFrame} in the given {@code angle}, according to the class
     * {@link ArrowRotator#ROTATION_MAP}. Also plays the {@link Sound#UI_BUTTON_CLICK} sound.
     *
     * @param angle The angle at which the arrow will point at.
     */
    private void rotate(int angle) {
        final Location location = this.frame.getLocation();
        this.frame.setRotation(ROTATION_MAP.get(angle));
        location.getWorld().playSound(location, Sound.UI_BUTTON_CLICK, 1f, 1f);
        ++this.itemPosition;
    }
}
