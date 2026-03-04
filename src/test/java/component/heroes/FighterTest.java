package component.heroes;

import component.Element;
import component.Target;
import component.Monster;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class FighterTest {

    @Test
    void testHealOnSkill() {
        Fighter f = new Fighter();
        f.setElement(Element.FIRE);
        Monster m = new Monster(1);

        f.setHp(100);
        f.skill(Target.one(m));

        // Fighter heals himself
        assertTrue(f.getHp() > 100);
    }

    @Test
    void testUltimateDamage() {
        Fighter f = new Fighter();
        f.setElement(Element.FIRE);
        Monster m = new Monster(1);

        double before = m.getHp();
        f.ultimate(Target.one(m));

        assertTrue(m.getHp() < before);
    }
}