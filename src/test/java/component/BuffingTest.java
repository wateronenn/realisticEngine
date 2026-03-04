package component.heroes;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class BuffingTest {

    @Test
    void testCasterBuffIncreaseAtk() {
        Caster caster = new Caster();
        Fighter target = new Fighter();

        double before = target.getAtkMul();

        // Caster buff applies 1.2 multiplier
        caster.buff(target);

        assertTrue(target.getAtkMul() > before);
        assertEquals(1.2, target.getAtkMul(), 0.0001);
    }

    @Test
    void testTankBuffIncreaseAtk() {
        Tank tank = new Tank();
        Fighter target = new Fighter();

        tank.buff(target);

        // Tank buff applies 1.1 multiplier
        assertEquals(1.1, target.getAtkMul(), 0.0001);
    }
}