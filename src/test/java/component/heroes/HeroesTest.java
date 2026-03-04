package component.heroes;

import component.Target;
import component.Unit;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * General unit tests for hero-related core mechanics.
 *
 * This test class verifies shared gameplay behaviors such as:
 * - Shield absorption
 * - Attack calculation with buffs
 * - Cooldown reduction
 * - Hero upgrade scaling
 * - State reset after a turn
 */
class HeroesTest {

    /**
     * Test that shield correctly absorbs incoming damage.
     * Damage should first reduce the shield before affecting HP.
     */
    @Test
    void testShieldAbsorb() {
        Tank t = new Tank();
        t.setShield(50);

        double damage = t.takeDamage(30);

        // Returned damage value should match absorbed amount
        assertEquals(30.0, damage, 0.0001);

        // Shield should decrease accordingly
        assertEquals(20.0, t.getShield(), 0.0001);
    }

    /**
     * Test that effective attack is calculated correctly
     * when attack multiplier and flat bonus are applied.
     */
    @Test
    void testEffectiveAtk() {
        Fighter f = new Fighter();
        f.applyAtkBuff(1.2, 10, 1);

        double expected = f.getAtk() * 1.2 + 10;

        // effectiveAtk() should reflect multiplier and flat bonus
        assertEquals(expected, f.effectiveAtk(), 0.0001);
    }

    /**
     * Test that cooldown decreases correctly after ticking.
     */
    @Test
    void testCooldownTick() {
        Fighter f = new Fighter();
        f.triggerSkillCd();
        f.tickCooldowns();

        // Skill cooldown should reduce by 1 turn
        assertEquals(1, f.getSkillCdRemain());
    }

    /**
     * Test that upgrading a hero increases core stats.
     * After upgrade(), max HP should be higher than before.
     */
    @Test
    void testUpgrade() {
        Fighter f = new Fighter();
        double oldHp = f.getMaxHp();

        f.upgrade();

        assertTrue(f.getMaxHp() > oldHp);
    }

    /**
     * Test that resetAfterTurn() restores necessary state.
     * Even if HP reaches 0, hero should be reset properly.
     * Skill cooldown should also be cleared.
     */
    @Test
    void testResetAfterTurn() {
        Fighter f = new Fighter();
        f.setHp(0);

        f.resetAfterTurn();

        // HP should be restored
        assertTrue(f.getHp() > 0);

        // Cooldown should reset
        assertEquals(0, f.getSkillCdRemain());
    }
}