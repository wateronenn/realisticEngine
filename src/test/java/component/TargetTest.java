package component;

import org.junit.jupiter.api.Test;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

class TargetTest {

    static class Dummy extends Unit {
        public Dummy() {
            super("Dummy",10,100,5);
        }
    }

    @Test
    void testSingleTarget() {
        Dummy d = new Dummy();
        Target t = Target.one(d);

        // Should be single target
        assertTrue(t.isSingle());
        assertFalse(t.isMany());
        assertEquals(d, t.single());
    }

    @Test
    void testManyTarget() {
        Dummy d1 = new Dummy();
        Dummy d2 = new Dummy();

        Target t = Target.many(List.of(d1,d2));

        // Should be many target
        assertTrue(t.isMany());
        assertFalse(t.isSingle());
        assertEquals(2, t.many().size());
    }
}