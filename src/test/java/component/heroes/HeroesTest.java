package component.heroes;

import component.Target;
import component.Unit;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class HeroesTest {

    @Test
    void testShieldAbsorb() {
        Tank t = new Tank();
        t.setShield(50);

        double damage = t.takeDamage(30);

        assertEquals(30.0, damage, 0.0001);
        assertEquals(20.0, t.getShield(), 0.0001);
    }

    @Test
    void testEffectiveAtk() {
        Fighter f = new Fighter();
        f.applyAtkBuff(1.2, 10, 1);

        double expected = f.getAtk() * 1.2 + 10;
        assertEquals(expected, f.effectiveAtk(), 0.0001);
    }

    @Test
    void testCooldownTick() {
        Fighter f = new Fighter();
        f.triggerSkillCd();
        f.tickCooldowns();

        assertEquals(1, f.getSkillCdRemain());
    }

    @Test
    void testUpgrade() {
        Fighter f = new Fighter();
        double oldHp = f.getMaxHp();

        f.upgrade();
        assertTrue(f.getMaxHp() > oldHp);
    }

    @Test
    void testResetAfterTurn() {
        Fighter f = new Fighter();
        f.setHp(0);
        f.resetAfterTurn();

        assertTrue(f.getHp() > 0);
        assertEquals(0, f.getSkillCdRemain());
    }
}