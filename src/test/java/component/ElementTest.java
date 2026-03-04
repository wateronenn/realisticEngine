package component;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class ElementTest {

    @Test
    void testSameElement() {
        assertEquals(0.8,
                Element.FIRE.getModifierAgainst(Element.FIRE),
                0.0001);
    }

    @Test
    void testAdvantage() {
        assertEquals(1.2,
                Element.FIRE.getModifierAgainst(Element.NATURE),
                0.0001);
    }

    @Test
    void testNeutral() {
        assertEquals(1.0,
                Element.FIRE.getModifierAgainst(Element.WATER),
                0.0001);
    }
}