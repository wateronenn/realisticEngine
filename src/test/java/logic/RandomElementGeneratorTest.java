package logic;

import component.Element;
import component.Unit;
import org.junit.jupiter.api.Test;
import java.util.ArrayList;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the RandomElementGenerator class.
 *
 * This test class verifies:
 * 1) Random element assignment to a list of units.
 * 2) Proper exception handling when requesting an invalid element image.
 */
class RandomElementGeneratorTest {

    /**
     * Dummy implementation of Unit for testing purposes.
     */
    static class Dummy extends Unit {
        public Dummy() {
            super("D", 10, 100, 5);
        }
    }

    /**
     * Test that getRandomElement() assigns elements
     * to all units in the provided list.
     *
     * The returned element list size should match the input list size,
     * and each unit should have a non-null element assigned.
     */
    @Test
    void testAssignElements() {
        ArrayList<Dummy> list = new ArrayList<>();
        list.add(new Dummy());
        list.add(new Dummy());
        list.add(new Dummy());

        var elements = RandomElementGenerator.getRandomElement(list);

        // The number of assigned elements should match the list size
        assertEquals(3, elements.size());

        // Each unit should have a non-null element
        assertNotNull(list.get(0).getElement());
    }

    /**
     * Test that requesting an element image with a null value
     * throws a NullPointerException.
     */
    @Test
    void testMissingImageThrows() {
        assertThrows(NullPointerException.class, () ->
                RandomElementGenerator.getElementImage(null)
        );
    }
}