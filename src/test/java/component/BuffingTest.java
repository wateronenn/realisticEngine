package component;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * BuffingTest
 *
 * Tests Buffing interface behavior.
 */
class BuffingTest {

    static class DummyUnit extends Unit {
        public DummyUnit() {
            super("Test", 10, 100, 5);
        }
    }

    static class Buffer implements Buffing {

        /**
         * Increases attack by 10.
         */
        @Override
        public void buff(Unit unit) {
            unit.setAtk(unit.getAtk() + 10);
        }
    }

    /**
     * Test buff increases attack value.
     */
    @Test
    void buffIncreasesAttack() {
        DummyUnit unit = new DummyUnit();
        Buffing buffer = new Buffer();

        buffer.buff(unit);

        assertEquals(20, unit.getAtk());
    }
}