package component.heroes;

import component.Element;
import component.Target;
import component.Monster;
import org.junit.jupiter.api.Test;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

class ArcherTest {

    @Test
    void testIncreaseBowStack() {
        Archer a = new Archer();
        a.increaseBowStack();
        a.increaseBowStack();

        // Bow stack should increase but max 5
        assertEquals(3, a.getBowStack());
    }

    @Test
    void testUltimateResetBow() {
        Archer a = new Archer();
        a.setElement(Element.FIRE);
        Monster m1 = new Monster(1);
        Monster m2 = new Monster(1);

        a.increaseBowStack();
        a.ultimate(Target.many(List.of(m1,m2)));

        // After ultimate stack resets to 1
        assertEquals(1, a.getBowStack());
    }
}