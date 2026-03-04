package component.heroes;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class HealingTest {

    @Test
    void testFighterHeal() {
        Fighter f = new Fighter();
        f.setHp(100);

        double before = f.getHp();

        // Fighter heals 0.8 * atk
        f.heal(f);

        assertTrue(f.getHp() > before);
    }

    @Test
    void testTankHeal() {
        Tank tank = new Tank();
        Fighter ally = new Fighter();

        ally.setHp(100);

        // Tank heals 30% of ally maxHp
        tank.heal(ally);

        double expected = 100 + 0.3 * ally.getMaxHp();
        assertEquals(expected, ally.getHp(), 0.0001);
    }
}