package logic;

import component.Element;
import component.Unit;
import org.junit.jupiter.api.Test;
import java.util.ArrayList;
import static org.junit.jupiter.api.Assertions.*;

class RandomElementGeneratorTest {

    static class Dummy extends Unit {
        public Dummy() { super("D",10,100,5); }
    }

    @Test
    void testAssignElements() {
        ArrayList<Dummy> list = new ArrayList<>();
        list.add(new Dummy());
        list.add(new Dummy());
        list.add(new Dummy());

        var elements = RandomElementGenerator.getRandomElement(list);

        // Should assign 3 elements
        assertEquals(3, elements.size());
        assertNotNull(list.get(0).getElement());
    }

    @Test
    void testMissingImageThrows() {
        assertThrows(NullPointerException.class, () ->
                RandomElementGenerator.getElementImage(null)
        );
    }
}