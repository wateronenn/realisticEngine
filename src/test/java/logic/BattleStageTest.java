package logic;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the BattleStage enum.
 *
 * This test class verifies:
 * 1) All expected battle stages exist and are accessible.
 * 2) Enum value lookup using valueOf() works correctly.
 */
class BattleStageTest {

    /**
     * Test that all defined BattleStage enum constants exist.
     * This ensures the battle state machine includes all required stages.
     */
    @Test
    void testAllBattleStagesExist() {
        // Verify each enum constant is accessible
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

    /**
     * Test that valueOf() correctly returns the enum constant
     * corresponding to the given string name.
     */
    @Test
    void testEnumValueOf() {
        BattleStage stage = BattleStage.valueOf("HERO_CHOOSE_SKILL");

        // valueOf should return the matching enum constant
        assertEquals(BattleStage.HERO_CHOOSE_SKILL, stage);
    }
}