package logic;

import component.Monster;
import component.heroes.Archer;
import component.heroes.Fighter;
import org.junit.jupiter.api.Test;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the BattleEngine class.
 *
 * This test class verifies:
 * 1) The initial battle stage after engine initialization.
 * 2) The basic hero skill selection flow.
 */
class BattleEngineTest {

    /**
     * Test that the initial battle stage is HERO_CHOOSE_SKILL
     * immediately after creating the BattleEngine.
     */
    @Test
    void testInitialStage() {
        BattleModel model = new BattleModel(
                List.of(new Archer(), new Fighter()),
                List.of(new Monster(1)));

        BattleEngine engine = new BattleEngine(model);

        // Engine should start at HERO_CHOOSE_SKILL stage
        assertEquals(BattleStage.HERO_CHOOSE_SKILL,
                engine.getBattleStage());
    }

    /**
     * Test hero skill selection flow.
     * When selecting a valid skill (e.g., NORMAL_ATTACK),
     * the method should return true indicating success.
     */
    @Test
    void testHeroChooseSkillFlow() {
        BattleModel model = new BattleModel(
                List.of(new Archer()),
                List.of(new Monster(1)));

        BattleEngine engine = new BattleEngine(model);

        boolean result = engine.onClickHeroSkill(SkillType.NORMAL_ATTACK);

        // Skill selection should succeed
        assertTrue(result);
    }
}