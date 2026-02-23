package component.heroes;

import component.Target;
import component.Unit;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * HeroesTest
 *
 * Tests shared hero mechanics:
 * - Shield
 * - Buff
 * - Cooldowns
 * - Upgrade
 */
class HeroesTest {

    static class DummyHero extends Heroes {

        public DummyHero() {
            super("Hero",50,200,10);
        }

        @Override
        public void normalAttack(Unit target) {}

        @Override
        public void skill(Target target) {}

        @Override
        public void ultimate(Target target) {}
    }

    DummyHero hero;

    @BeforeEach
    void setup() {
        hero = new DummyHero();
    }

    /**
     * Test shield absorbs incoming damage before HP.
     */
    @Test
    void shieldAbsorbsDamage() {
        hero.setShield(30);
        hero.takeDamage(40);

        assertEquals(190, hero.getHp());
        assertEquals(0, hero.getShield());
    }

    /**
     * Test attack buff increases effective attack.
     */
    @Test
    void atkBuffWorks() {
        hero.applyAtkBuff(2.0, 10, 1);
        assertEquals(110, hero.effectiveAtk());
    }

    /**
     * Test buff expires after turn ends.
     */
    @Test
    void buffExpiresAfterTurn() {
        hero.applyAtkBuff(2.0, 10, 1);
        hero.onTurnEnd();

        assertEquals(50, hero.effectiveAtk());
    }

    /**
     * Test upgrade increases stats.
     */
    @Test
    void upgradeIncreasesStats() {
        double oldHp = hero.getMaxHp();
        hero.upgrade();

        assertTrue(hero.getMaxHp() > oldHp);
    }

    /**
     * Test cooldown system.
     */
    @Test
    void cooldownSystemWorks() {
        hero.triggerSkillCd();
        assertFalse(hero.canUseSkill());

        hero.resetAllCooldowns();
        assertTrue(hero.canUseSkill());
    }
}