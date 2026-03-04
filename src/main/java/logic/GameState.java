package logic;

/**
 * Represents the high-level states of the game lifecycle.
 *
 * <p>The {@link GameEngine} transitions between these states to control
 * the overall flow of the game outside the battle engine. Each state
 * corresponds to a different screen or phase of gameplay.
 *
 * <p>Typical progression:
 * <pre>
 * START_GAME
 * → SELECT_TEAM
 * → ROLL_ELEMENT
 * → BATTLE
 * → VICTORY
 * → UPGRADE
 * → BATTLE (next stage)
 * → DEFEAT (if player loses)
 * </pre>
 */
public enum GameState {

    /** Initial state when the game starts before any interaction occurs. */
    START_GAME,

    /** Player selects heroes to form the team before entering battle. */
    SELECT_TEAM,

    /**
     * State where the player rolls or assigns elemental bonuses
     * to heroes before combat begins.
     */
    ROLL_ELEMENT,

    /** Active battle phase controlled by the {@link BattleEngine}. */
    BATTLE,

    /** State reached when all monsters in the current stage are defeated. */
    VICTORY,

    /**
     * State where the player upgrades a hero after winning a battle
     * before progressing to the next stage.
     */
    UPGRADE,

    /** Final state triggered when all heroes are defeated. */
    DEFEAT
}