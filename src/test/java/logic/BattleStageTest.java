package logic;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class BattleStageTest {

    @Test
    void testAllBattleStagesExist() {
        // Ensure enum values are accessible
        assertNotNull(BattleStage.START_BATTLE);
        assertNotNull(BattleStage.HERO_CHOOSE_SKILL);
        assertNotNull(BattleStage.HERO_CHOOSE_TARGET);
        assertNotNull(BattleStage.HERO_CHOOSE_ALLY);
        assertNotNull(BattleStage.HERO_RESOLVE_ACTION);
        assertNotNull(BattleStage.MONSTER_TURN);
        assertNotNull(BattleStage.CHECK_END);
        assertNotNull(BattleStage.WIN_TURN);
        assertNotNull(BattleStage.LOSE_TURN);
    }

    @Test
    void testEnumValueOf() {
        BattleStage stage = BattleStage.valueOf("HERO_CHOOSE_SKILL");

        // valueOf should return correct enum
        assertEquals(BattleStage.HERO_CHOOSE_SKILL, stage);
    }
}