package component;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * HealingTest
 *
 * Tests Healing interface behavior.
 */
class HealingTest {

    static class DummyUnit extends Unit {
        public DummyUnit() {
            super("Test", 10, 100, 5);
        }
    }

    static class Healer implements Healing {

        /**
         * Heals target by 20 HP.
         */
        @Override
        public void heal(Unit target) {
            target.setHp(target.getHp() + 20);
        }
    }

    /**
     * Test healing increases HP correctly.
     */
    @Test
    void healingIncreasesHp() {
        DummyUnit unit = new DummyUnit();
        unit.setHp(50);

        Healing healer = new Healer();
        healer.heal(unit);

        assertEquals(70, unit.getHp());
    }
}