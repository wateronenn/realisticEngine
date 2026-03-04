package logic;

/**
 * Represents the targeting rules used by skills and abilities in battle.
 *
 * <p>This enum defines how a skill selects its targets. The
 * {@link BattleEngine} uses this information to determine whether
 * the player must select a target manually or if the engine should
 * automatically apply the skill to one or more units.
 *
 * <p>Different hero abilities may require different targeting types
 * depending on their role or skill behavior.
 */
public enum TargetType {

    /**
     * A single enemy must be selected as the target.
     */
    ENEMY_ONE,

    /**
     * All enemies will be targeted automatically.
     */
    ENEMY_ALL,

    /**
     * A single ally must be selected as the target.
     */
    ALLY_ONE,

    /**
     * All allied heroes will be targeted automatically.
     */
    ALLY_ALL,

    /**
     * The skill targets the hero casting the ability.
     */
    SELF,

    /**
     * No target selection is required.
     * The skill applies its effect automatically.
     */
    NONE
}