package logic;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class GameStateTest {

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

    @Test
    void testValueOf() {
        GameState state = GameState.valueOf("BATTLE");

        // valueOf should return correct enum
        assertEquals(GameState.BATTLE, state);
    }
}