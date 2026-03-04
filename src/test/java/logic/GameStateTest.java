package logic;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the GameState enum.
 *
 * This test class verifies:
 * 1) All expected game states exist and are accessible.
 * 2) Enum lookup using valueOf() works correctly.
 */
class GameStateTest {

    /**
     * Test that all defined GameState enum constants exist.
     * This ensures the game flow state machine includes all required states.
     */
    @Test
    void testAllGameStatesExist() {
        assertNotNull(GameState.START_GAME);
        assertNotNull(GameState.SELECT_TEAM);
        assertNotNull(GameState.ROLL_ELEMENT);
        assertNotNull(GameState.BATTLE);
        assertNotNull(GameState.VICTORY);
        assertNotNull(GameState.UPGRADE);
        assertNotNull(GameState.DEFEAT);
    }

    /**
     * Test that valueOf() correctly returns the enum constant
     * corresponding to the provided string name.
     */
    @Test
    void testValueOf() {
        GameState state = GameState.valueOf("BATTLE");

        // valueOf should return the matching enum constant
        assertEquals(GameState.BATTLE, state);
    }
}