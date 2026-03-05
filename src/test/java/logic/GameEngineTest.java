package logic;

import component.heroes.Archer;
import component.heroes.Heroes;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the GameEngine class.
 *
 * This test class verifies:
 * 1) Team member toggling functionality.
 * 2) Maximum team size validation.
 * 3) Stage counter management.
 */
class GameEngineTest {

    /**
     * Test that toggleTeamMember() correctly adds and removes
     * a hero from the team.
     *
     * First toggle → hero is added.
     * Second toggle → hero is removed.
     */
    @Test
    void testToggleTeamMember() {
        GameEngine engine = new GameEngine();
        Heroes h = GameEngine.getAvailableHeroes().get(0);

        // Add hero to team
        assertTrue(GameEngine.toggleTeamMember(h));
        assertTrue(GameEngine.isInTeam(h));

        // Remove hero from team
        assertTrue(GameEngine.toggleTeamMember(h));
        assertFalse(GameEngine.isInTeam(h));
    }

    /**
     * Test that the team cannot exceed the maximum allowed size.
     * After toggling all available heroes into the team,
     * checkFullTeam() should return true.
     */
    @Test
    void testMaxTeamLimit() {
        GameEngine engine = new GameEngine();

        for (Heroes h : GameEngine.getAvailableHeroes()) {
            GameEngine.toggleTeamMember(h);
        }

        // Team should be considered full
        assertTrue(GameEngine.checkFullTeam());
    }

    /**
     * Test stage counter increment logic.
     * Setting stage to 1 and adding 1 should result in 2.
     */
    @Test
    void testStageCounter() {
        GameEngine.setStageCounter(1);
        GameEngine.addStageCounter(1);

        // Stage counter should increase correctly
        assertEquals(2, GameEngine.getStageCounter());
    }
}