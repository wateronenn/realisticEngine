package component;

import component.heroes.Caster;
import component.heroes.Fighter;
import component.heroes.Tank;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for buffing mechanics.
 *
 * This test class verifies that different heroes correctly apply
 * attack multiplier buffs to their targets.
 */
class BuffingTest {

    /**
     * Test that Caster's buff correctly increases
     * the target's attack multiplier to 1.2.
     */
    @Test
    void testCasterBuffIncreaseAtk() {
        Caster caster = new Caster();
        Fighter target = new Fighter();

        double before = target.getAtkMul();

        // Apply Caster's buff to the target
        caster.buff(target);

        // Attack multiplier should increase
        assertTrue(target.getAtkMul() > before);

        // Expected multiplier is 1.2
        assertEquals(1.2, target.getAtkMul(), 0.0001);
    }

    /**
     * Test that Tank's buff correctly increases
     * the target's attack multiplier to 1.1.
     */
    @Test
    void testTankBuffIncreaseAtk() {
        Tank tank = new Tank();
        Fighter target = new Fighter();

        // Apply Tank's buff to the target
        tank.buff(target);

        // Expected multiplier is 1.1
        assertEquals(1.1, target.getAtkMul(), 0.0001);
    }
}