package component.heroes;

import component.Element;
import component.Target;
import component.Monster;
import org.junit.jupiter.api.Test;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the Archer class.
 *
 * This test class verifies the behavior of the bow stack mechanic
 * and ensures that the ultimate skill correctly resets the stack.
 */
class ArcherTest {

    /**
     * Test that increasing the bow stack works correctly.
     * The stack should increment each time increaseBowStack() is called.
     * Initial stack is expected to be 1, so after two increases it should be 3.
     * (The maximum allowed stack is 5.)
     */
    @Test
    void testIncreaseBowStack() {
        Archer a = new Archer();
        a.increaseBowStack();
        a.increaseBowStack();

        // Bow stack should increase but not exceed the maximum limit
        assertEquals(3, a.getBowStack());
    }

    /**
     * Test that using the ultimate skill resets the bow stack.
     * After calling ultimate(), the stack should return to 1.
     */
    @Test
    void testUltimateResetBow() {
        Archer a = new Archer();
        a.setElement(Element.FIRE);

        Monster m1 = new Monster(1);
        Monster m2 = new Monster(1);

        a.increaseBowStack();
        a.ultimate(Target.many(List.of(m1, m2)));

        // After ultimate is used, bow stack should reset to 1
        assertEquals(1, a.getBowStack());
    }
}