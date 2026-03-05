package component;

/**
 * Represents elemental types used in the combat system.
 *
 * <p>Each element may have a damage modifier against another element.
 * The modifier is applied during attack calculation.</p>
 *
 * <p>Damage rules:</p>
 * <ul>
 *     <li>If attacker and target have the same element → 0.8× damage</li>
 *     <li>If attacker has advantage over target → 1.2× damage</li>
 *     <li>Otherwise → 1.0× damage</li>
 * </ul>
 *
 * <p>Element advantages:</p>
 * <ul>
 *     <li>WATER > FIRE</li>
 *     <li>FIRE > NATURE</li>
 *     <li>NATURE > WATER</li>
 *     <li>LIGHT > DARK</li>
 *     <li>DARK > LIGHT</li>
 * </ul>
 */
public enum Element {

    WATER,
    FIRE,
    NATURE,
    LIGHT,
    DARK;

    /**
     * Returns the damage modifier when this element attacks the target element.
     *
     * @param target the target's element
     * @return damage multiplier (e.g., 1.2 for advantage, 0.8 for same element, 1.0 otherwise)
     */
    public double getModifierAgainst(Element target) {

        if (this == target) return 0.8;

        switch (this) {
            case WATER:
                return target == FIRE ? 1.2 : 1.0;

            case FIRE:
                return target == NATURE ? 1.2 : 1.0;

            case NATURE:
                return target == WATER ? 1.2 : 1.0;

            case LIGHT:
                return target == DARK ? 1.2 : 1.0;

            case DARK:
                return target == LIGHT ? 1.2 : 1.0;

            default:
                return 1.0;
        }
    }
}