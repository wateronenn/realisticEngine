package logic;

import component.Monster;
import component.heroes.Archer;
import component.heroes.Fighter;
import org.junit.jupiter.api.Test;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

class BattleEngineTest {

    @Test
    void testInitialStage() {
        BattleModel model = new BattleModel(
                List.of(new Archer(), new Fighter()),
                List.of(new Monster(1)));

        BattleEngine engine = new BattleEngine(model);

        assertEquals(BattleStage.HERO_CHOOSE_SKILL,
                engine.getBattleStage());
    }

    @Test
    void testHeroChooseSkillFlow() {
        BattleModel model = new BattleModel(
                List.of(new Archer()),
                List.of(new Monster(1)));

        BattleEngine engine = new BattleEngine(model);

        boolean result = engine.onClickHeroSkill(SkillType.NORMAL_ATTACK);

        assertTrue(result);
    }
}