package component;

import java.util.List;

/**
 * Represents a target container used for skill execution.
 *
 * <p>A {@code Target} instance can represent either:
 * <ul>
 *   <li>a single {@link Unit}</li>
 *   <li>a group of {@link Unit} objects</li>
 * </ul>
 *
 * <p>This abstraction allows skills to accept a unified parameter
 * while supporting both single-target and multi-target abilities.</p>
 *
 * <p>Exactly one of {@link #single()} or {@link #many()} will be non-null.</p>
 */
public final class Target {

    /** Single target unit (used for single-target skills). */
    private final Unit single;

    /** Group of target units (used for multi-target skills). */
    private final List<? extends Unit> group;

    /**
     * Private constructor. Use factory methods {@link #one(Unit)} or {@link #many(List)}.
     *
     * @param single single unit target (or null)
     * @param group  list of target units (or null)
     */
    private Target(Unit single, List<? extends Unit> group) {
        this.single = single;
        this.group = group;
    }

    /**
     * Creates a Target representing a single unit.
     *
     * @param u the target unit
     * @return a Target in single-target mode
     */
    public static Target one(Unit u) {
        return new Target(u, null);
    }

    /**
     * Creates a Target representing multiple units.
     *
     * @param units list of target units
     * @return a Target in multi-target mode
     */
    public static Target many(List<? extends Unit> units) {
        return new Target(null, units);
    }

    /**
     * Returns the single target.
     *
     * @return single unit, or null if this is a multi-target
     */
    public Unit single() {
        return single;
    }

    /**
     * Returns the list of targets.
     *
     * @return list of units, or null if this is a single-target
     */
    public List<? extends Unit> many() {
        return group;
    }

    /**
     * Checks whether this Target represents a single unit.
     *
     * @return true if single-target mode
     */
    public boolean isSingle() {
        return single != null;
    }

    /**
     * Checks whether this Target represents multiple units.
     *
     * @return true if multi-target mode
     */
    public boolean isMany() {
        return group != null;
    }
}