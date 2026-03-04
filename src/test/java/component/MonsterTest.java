package component;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class MonsterTest {

    @Test
    void testBaseStatsType1() {
        Monster m = new Monster(1);
        assertEquals("Type1", m.getName());
        assertEquals(35, m.getAtk(), 0.0001);
        assertEquals(400, m.getMaxHp(), 0.0001);
    }

    @Test
    void testScaleToStage() {
        Monster m = new Monster(1);
        m.scaleToStage(2);

        assertTrue(m.getMaxHp() > 400);
        assertTrue(m.getAtk() > 35);
    }

    @Test
    void testAttack() {
        Monster m = new Monster(1);
        m.setElement(Element.FIRE);
        Monster target = new Monster(1);

        double damage = m.attack(target);
        assertTrue(damage > 0);
    }
}