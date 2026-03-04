package logic;

/**
 * Represents the different stages of the battle state machine.
 *
 * <p>The {@link BattleEngine} transitions between these stages to control
 * the flow of a turn-based battle. Each stage represents a specific phase
 * of interaction between the player, heroes, monsters, and the game system.
 *
 * <p>Typical battle flow:
 * <pre>
 * START_BATTLE
 * → HERO_CHOOSE_SKILL
 * → HERO_CHOOSE_TARGET / HERO_CHOOSE_ALLY (if needed)
 * → HERO_RESOLVE_ACTION
 * → MONSTER_TURN
 * → CHECK_END
 * → HERO_CHOOSE_SKILL (next round)
 * → WIN_TURN or LOSE_TURN
 * </pre>
 */
public enum BattleStage {

    /** Initial stage when the battle begins and the system prepares the first turn. */
    START_BATTLE,

    /** Player chooses which skill the active hero will use. */
    HERO_CHOOSE_SKILL,

    /** Player selects a monster target for a single-target skill or attack. */
    HERO_CHOOSE_TARGET,

    /** Player selects an ally hero as the target of a skill (for example healing or buffs). */
    HERO_CHOOSE_ALLY,

    /** The engine resolves the chosen hero action and applies its effects. */
    HERO_RESOLVE_ACTION,

    /** Monsters perform their attacks after all heroes finish their actions for the round. */
    MONSTER_TURN,

    /** Intermediate stage where the engine checks victory or defeat conditions. */
    CHECK_END,

    /** Stage indicating that all monsters are defeated and the player wins the battle. */
    WIN_TURN,

    /** Stage indicating that all heroes are defeated and the player loses the battle. */
    LOSE_TURN
}