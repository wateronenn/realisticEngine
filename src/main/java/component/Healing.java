package component;

/**
 * Represents a healing capability.
 *
 * <p>Classes implementing this interface are able to restore
 * health points (HP) to a {@link Unit}.</p>
 *
 * <p>The exact healing formula, scaling, and additional effects
 * depend on the implementing class.</p>
 */
public interface Healing {

    /**
     * Applies a healing effect to the specified target unit.
     *
     * @param target the unit to be healed
     */
    void heal(Unit target);
}