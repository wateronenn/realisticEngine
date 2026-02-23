package component.heroes;

import component.Target;
import component.Unit;
import logic.SkillType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * FighterTest
 *
 * Tests Fighter behaviors:
 * - Constructor
 * - Normal attack
 * - Skill
 * - Ultimate
 * - castSkill switch
 */
class FighterTest {

    Fighter fighter;
    Unit dummy;

    static class DummyUnit extends Unit {
        public DummyUnit() {
            super("Dummy", 20, 200, 5);
        }
    }

    @BeforeEach
    void setup() {
        fighter = new Fighter();
        dummy = new DummyUnit();
    }

    /**
     * Test constructor sets hero class correctly.
     */
    @Test
    void constructorSetsHeroClass() {
        assertEquals("Fighter", fighter.getHeroClass());
    }

    /**
     * Test normal attack reduces enemy HP.
     */
    @Test
    void normalAttackDealsDamage() {
        double before = dummy.getHp();

        fighter.normalAttack(dummy);

        assertTrue(dummy.getHp() < before);
    }

    /**
     * Test skill triggers cooldown.
     */
    @Test
    void skillTriggersCooldown() {
        fighter.skill(Target.one(dummy));

        assertFalse(fighter.canUseSkill());
    }

    /**
     * Test ultimate triggers cooldown.
     */
    @Test
    void ultimateTriggersCooldown() {
        fighter.ultimate(Target.one(dummy));

        assertFalse(fighter.canUseUlt());
    }

    /**
     * Test castSkill ULTIMATE branch.
     */
    @Test
    void castSkillUltimateBranchWorks() {
        fighter.castSkill(SkillType.ULTIMATE, Target.one(dummy));

        assertFalse(fighter.canUseUlt());
    }
}