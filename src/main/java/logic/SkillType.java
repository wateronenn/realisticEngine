package logic;

/**
 * Represents the different types of actions a hero can perform during battle.
 *
 * <p>Each skill type determines how the {@link BattleEngine} resolves the action
 * and what targeting behavior may be required. Heroes may have cooldown
 * restrictions on certain skill types.
 *
 * <p>These values are typically used when the player selects a skill in the UI
 * and the engine determines how the action should be executed.
 */
public enum SkillType {

    /**
     * Basic attack that targets a single enemy and has no cooldown.
     */
    NORMAL_ATTACK,

    /**
     * Standard hero ability that may have a cooldown and specific targeting rules.
     */
    SKILL,

    /**
     * Powerful hero ability with a longer cooldown and often stronger effects.
     */
    ULTIMATE
}