package component.heroes;

import component.Target;
import component.Unit;
import logic.SkillType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * TankTest
 *
 * This class tests all behaviors of Tank hero:
 * - Constructor values
 * - Normal attack
 * - Skill
 * - Ultimate
 * - Shield mechanics
 * - castSkill switch behavior
 */
class TankTest {

    Tank tank;        // Tank instance for testing
    Unit dummy;       // Dummy enemy target

    /**
     * Dummy concrete Unit because Unit is abstract.
     */
    static class DummyUnit extends Unit {
        public DummyUnit() {
            super("Dummy", 20, 200, 5);
        }
    }

    /**
     * Runs before each test to reset state.
     */
    @BeforeEach
    void setup() {
        tank = new Tank();
        dummy = new DummyUnit();
    }

    /**
     * Test constructor correctly sets hero class.
     */
    @Test
    void constructorSetsCorrectHeroClass() {
        assertEquals("Tank", tank.getHeroClass());
    }

    /**
     * Test normal attack reduces enemy HP.
     */
    @Test
    void normalAttackDealsDamage() {
        double before = dummy.getHp();

        tank.normalAttack(dummy);

        assertTrue(dummy.getHp() < before);
    }

    /**
     * Test shield absorbs damage before HP.
     */
    @Test
    void shieldAbsorbsDamageBeforeHp() {
        tank.setShield(50);

        double beforeHp = tank.getHp();

        tank.takeDamage(30);

        // HP should not decrease because shield absorbs
        assertEquals(beforeHp, tank.getHp());

        // Shield should decrease
        assertEquals(20, tank.getShield());
    }

    /**
     * Test skill triggers cooldown.
     */
    @Test
    void skillTriggersCooldown() {
        tank.skill(Target.one(dummy));

        assertFalse(tank.canUseSkill());
    }

    /**
     * Test ultimate triggers cooldown.
     */
    @Test
    void ultimateTriggersCooldown() {
        tank.ultimate(Target.one(dummy));

        assertFalse(tank.canUseUlt());
    }

    /**
     * Test castSkill NORMAL_ATTACK calls normalAttack().
     */
    @Test
    void castSkillNormalAttackWorks() {
        double before = dummy.getHp();

        tank.castSkill(SkillType.NORMAL_ATTACK, Target.one(dummy));

        assertTrue(dummy.getHp() < before);
    }
}