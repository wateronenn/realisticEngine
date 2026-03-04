package component;

import org.junit.jupiter.api.Test;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the Target class.
 *
 * This test class verifies that Target correctly handles:
 * 1) Single target creation.
 * 2) Multiple target creation.
 */
class TargetTest {

    /**
     * Dummy class extending Unit for testing purposes.
     * Provides minimal implementation required for Target testing.
     */
    static class Dummy extends Unit {
        public Dummy() {
            super("Dummy", 10, 100, 5);
        }
    }

    /**
     * Test creation of a single target using Target.one().
     * The Target instance should behave as a single target.
     */
    @Test
    void testSingleTarget() {
        Dummy d = new Dummy();
        Target t = Target.one(d);

        // Verify single-target behavior
        assertTrue(t.isSingle());
        assertFalse(t.isMany());
        assertEquals(d, t.single());
    }

    /**
     * Test creation of multiple targets using Target.many().
     * The Target instance should behave as a multi-target container.
     */
    @Test
    void testManyTarget() {
        Dummy d1 = new Dummy();
        Dummy d2 = new Dummy();

        Target t = Target.many(List.of(d1, d2));

        // Verify multi-target behavior
        assertTrue(t.isMany());
        assertFalse(t.isSingle());
        assertEquals(2, t.many().size());
    }
}