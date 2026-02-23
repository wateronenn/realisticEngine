package component;

import org.junit.jupiter.api.Test;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

/**
 * TargetTest
 *
 * Tests single and multiple target creation
 * and validation methods.
 */
class TargetTest {

    static class DummyUnit extends Unit {
        public DummyUnit(String n) {
            super(n, 10, 100, 5);
        }
    }

    /**
     * Test single target creation.
     */
    @Test
    void singleTargetWorks() {
        Unit u = new DummyUnit("A");
        Target t = Target.one(u);

        assertTrue(t.isSingle());
        assertFalse(t.isMany());
        assertEquals(u, t.single());
    }

    /**
     * Test multiple target creation.
     */
    @Test
    void multipleTargetWorks() {
        Unit u1 = new DummyUnit("A");
        Unit u2 = new DummyUnit("B");

        Target t = Target.many(List.of(u1,u2));

        assertTrue(t.isMany());
        assertFalse(t.isSingle());
        assertEquals(2, t.many().size());
    }
}