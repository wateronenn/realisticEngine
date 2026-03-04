package component;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class UnitTest {

    static class DummyUnit extends Unit {
        public DummyUnit() {
            super("Dummy", 50, 100, 10);
        }
    }

    @Test
    void testDamageReduction() {
        // damage lower than def → should be 0
        DummyUnit u = new DummyUnit();
        assertEquals(0.0, u.dmgReduction(5), 0.0001);

        // damage higher than def → dmg - def
        assertEquals(40.0, u.dmgReduction(50), 0.0001);
    }

    @Test
    void testTakeDamage() {
        DummyUnit u = new DummyUnit();
        double dealt = u.takeDamage(50); // 50 - 10 def = 40

        assertEquals(40.0, dealt, 0.0001);
        assertEquals(60.0, u.getHp(), 0.0001);
    }

    @Test
    void testHpClamp() {
        DummyUnit u = new DummyUnit();
        u.setHp(200); // exceed maxHp
        assertEquals(100.0, u.getHp(), 0.0001);

        u.setHp(-50);
        assertEquals(0.0, u.getHp(), 0.0001);
    }

    @Test
    void testIsDead() {
        DummyUnit u = new DummyUnit();
        u.setHp(0);
        assertTrue(u.isDead());
    }

    @Test
    void testAttackWithElementModifier() {
        DummyUnit attacker = new DummyUnit();
        DummyUnit target = new DummyUnit();

        attacker.setElement(Element.FIRE);
        target.setElement(Element.NATURE);

        double damage = attacker.attack(target, 50);
        // FIRE vs NATURE → 1.2 multiplier
        // 50 * 1.2 = 60 → minus 10 def = 50 actual

        assertEquals(50.0, damage, 0.0001);
    }

    @Test
    void testHpPercent() {
        DummyUnit u = new DummyUnit();
        u.setHp(50);
        assertEquals(50.0, u.getHpPercent(), 0.0001);
    }
}