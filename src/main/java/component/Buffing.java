package component;

/**
 * Represents a buffing capability.
 *
 * <p>Classes implementing this interface are able to apply
 * a temporary or permanent enhancement (buff) to a {@link Unit}.</p>
 *
 * <p>The specific effect of the buff (e.g., attack increase,
 * shield, defense boost, duration) depends on the implementing class.</p>
 */
public interface Buffing {

    /**
     * Applies a buff effect to the specified unit.
     *
     * @param unit the target unit to receive the buff
     */
    void buff(Unit unit);
}