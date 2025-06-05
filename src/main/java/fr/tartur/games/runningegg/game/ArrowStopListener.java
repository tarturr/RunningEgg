package fr.tartur.games.runningegg.game;

/**
 * Interface used on classes which needs to know when the arrow of the {@link ArrowRotator} stops spinning.
 *
 * @see ArrowRotator
 */
public interface ArrowStopListener {

    /**
     * Method called when the arrow of the {@link ArrowRotator} stops spinning.
     */
    void onArrowStop();

}
