package component;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the Element class.
 *
 * This test class verifies that the getModifierAgainst() method
 * correctly returns the damage modifier based on element interactions.
 */
class ElementTest {

    /**
     * Test when attacking the same element.
     * FIRE attacking FIRE should return 0.8 (reduced damage).
     */
    @Test
    void testSameElement() {
        assertEquals(0.8,
                Element.FIRE.getModifierAgainst(Element.FIRE),
                0.0001); // delta for double comparison
    }

    /**
     * Test elemental advantage.
     * FIRE attacking NATURE should return 1.2 (increased damage).
     */
    @Test
    void testAdvantage() {
        assertEquals(1.2,
                Element.FIRE.getModifierAgainst(Element.NATURE),
                0.0001);
    }

    /**
     * Test neutral interaction.
     * FIRE attacking WATER should return 1.0 (normal damage).
     */
    @Test
    void testNeutral() {
        assertEquals(1.0,
                Element.FIRE.getModifierAgainst(Element.WATER),
                0.0001);
    }
}