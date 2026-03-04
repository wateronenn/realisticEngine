package component;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the Unit base class.
 *
 * This test class verifies core combat mechanics including:
 * - Damage reduction calculation
 * - Damage application
 * - HP boundary clamping
 * - Death status detection
 * - Elemental damage modifier interaction
 * - HP percentage calculation
 */
class UnitTest {

    /**
     * Dummy implementation of Unit used for testing.
     * Base stats:
     * ATK = 50
     * Max HP = 100
     * DEF = 10
     */
    static class DummyUnit extends Unit {
        public DummyUnit() {
            super("Dummy", 50, 100, 10);
        }
    }

    /**
     * Test damage reduction logic.
     * - If incoming damage is lower than DEF → result should be 0.
     * - If incoming damage is higher than DEF → result = damage - DEF.
     */
    @Test
    void testDamageReduction() {
        DummyUnit u = new DummyUnit();

        // Damage lower than defense → no effective damage
        assertEquals(0.0, u.dmgReduction(5), 0.0001);

        // Damage higher than defense → reduced by DEF
        assertEquals(40.0, u.dmgReduction(50), 0.0001);
    }

    /**
     * Test takeDamage() correctly applies reduced damage to HP.
     * 50 incoming damage → 50 - 10 DEF = 40 actual damage.
     */
    @Test
    void testTakeDamage() {
        DummyUnit u = new DummyUnit();
        double dealt = u.takeDamage(50);

        // Returned damage should match calculated effective damage
        assertEquals(40.0, dealt, 0.0001);

        // HP should decrease accordingly (100 - 40 = 60)
        assertEquals(60.0, u.getHp(), 0.0001);
    }

    /**
     * Test HP clamping behavior.
     * HP should not exceed maxHp and should not go below 0.
     */
    @Test
    void testHpClamp() {
        DummyUnit u = new DummyUnit();

        // Exceeding max HP should clamp to maxHp
        u.setHp(200);
        assertEquals(100.0, u.getHp(), 0.0001);

        // Negative HP should clamp to 0
        u.setHp(-50);
        assertEquals(0.0, u.getHp(), 0.0001);
    }

    /**
     * Test isDead() returns true when HP is 0.
     */
    @Test
    void testIsDead() {
        DummyUnit u = new DummyUnit();
        u.setHp(0);

        assertTrue(u.isDead());
    }

    /**
     * Test attack calculation with elemental modifier.
     * FIRE attacking NATURE → 1.2 multiplier.
     *
     * Calculation:
     * 50 base damage × 1.2 = 60
     * 60 - 10 DEF = 50 final damage
     */
    @Test
    void testAttackWithElementModifier() {
        DummyUnit attacker = new DummyUnit();
        DummyUnit target = new DummyUnit();

        attacker.setElement(Element.FIRE);
        target.setElement(Element.NATURE);

        double damage = attacker.attack(target, 50);

        assertEquals(50.0, damage, 0.0001);
    }

    /**
     * Test HP percentage calculation.
     * 50 HP out of 100 max HP → 50%.
     */
    @Test
    void testHpPercent() {
        DummyUnit u = new DummyUnit();
        u.setHp(50);

        assertEquals(50.0, u.getHpPercent(), 0.0001);
    }
}