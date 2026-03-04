package component;

import component.heroes.Fighter;
import component.heroes.Tank;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for healing mechanics of different hero classes.
 *
 * This test class verifies that healing abilities correctly
 * restore HP based on each hero's healing formula.
 */
class HealingTest {

    /**
     * Test Fighter's self-healing ability.
     * Fighter should heal based on 0.8 * atk.
     * After healing, HP should be greater than before.
     */
    @Test
    void testFighterHeal() {
        Fighter f = new Fighter();
        f.setHp(100);

        double before = f.getHp();

        // Fighter heals himself
        f.heal(f);

        // HP should increase after healing
        assertTrue(f.getHp() > before);
    }

    /**
     * Test Tank's healing ability on an ally.
     * Tank heals 30% of the ally's max HP.
     */
    @Test
    void testTankHeal() {
        Tank tank = new Tank();
        Fighter ally = new Fighter();

        ally.setHp(100);

        // Tank heals the ally
        tank.heal(ally);

        double expected = 100 + 0.3 * ally.getMaxHp();

        // Ally's HP should match the expected healed amount
        assertEquals(expected, ally.getHp(), 0.0001);
    }
}