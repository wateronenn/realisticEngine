package logic;

import component.heroes.Archer;
import component.heroes.Heroes;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class GameEngineTest {

    @Test
    void testToggleTeamMember() {
        GameEngine engine = new GameEngine();
        Heroes h = GameEngine.getAvailableHeroes().get(0);

        assertTrue(GameEngine.toggleTeamMember(h));
        assertTrue(GameEngine.isInTeam(h));

        assertTrue(GameEngine.toggleTeamMember(h));
        assertFalse(GameEngine.isInTeam(h));
    }

    @Test
    void testMaxTeamLimit() {
        GameEngine engine = new GameEngine();
        for (Heroes h : GameEngine.getAvailableHeroes()) {
            GameEngine.toggleTeamMember(h);
        }
        assertTrue(GameEngine.checkFullTeam());
    }

    @Test
    void testStageCounter() {
        GameEngine.setStageCounter(1);
        GameEngine.addStageCounter(1);
        assertEquals(2, GameEngine.getStageCounter());
    }
}