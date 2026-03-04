package logic;

/**
 * Represents the different hero archetypes available in the battle system.
 *
 * <p>Each character type defines a unique combat role and typically determines
 * the behavior of skills, targeting rules, and combat strategy used during battle.
 *
 * <p>These character types are used by the battle engine to determine
 * how skills behave and what targets they affect.
 */
public enum Character {

    /**
     * Magic-based attacker that specializes in area damage
     * and offensive spells that often target multiple enemies.
     */
    CASTER,

    /**
     * Ranged damage dealer that attacks enemies from a distance.
     * Typically focuses on high damage output or multi-target attacks.
     */
    ARCHER,

    /**
     * Defensive frontline hero that protects allies through
     * shields, healing, or supportive abilities.
     */
    TANK,

    /**
     * Balanced melee fighter specializing in direct combat
     * and single-target damage against enemies.
     */
    FIGHTER
}