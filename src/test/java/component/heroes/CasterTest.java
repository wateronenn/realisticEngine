package component.heroes;

import component.Target;
import component.Unit;
import logic.SkillType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * CasterTest
 *
 * Tests all behaviors of Caster:
 * - Constructor
 * - Normal attack
 * - Skill
 * - Ultimate (AOE damage)
 * - castSkill logic
 */
class CasterTest {

    Caster caster;
    Unit dummy;

    static class DummyUnit extends Unit {
        public DummyUnit() {
            super("Dummy", 15, 200, 5);
        }
    }

    @BeforeEach
    void setup() {
        caster = new Caster();
        dummy = new DummyUnit();
    }

    /**
     * Test constructor sets hero class correctly.
     */
    @Test
    void constructorSetsHeroClass() {
        assertEquals("Caster", caster.getHeroClass());
    }

    /**
     * Test normal attack reduces enemy HP.
     */
    @Test
    void normalAttackDealsDamage() {
        double before = dummy.getHp();

        caster.normalAttack(dummy);

        assertTrue(dummy.getHp() < before);
    }

    /**
     * Test ultimate damages multiple targets.
     */
    @Test
    void ultimateHitsMultipleTargets() {
        Unit d2 = new DummyUnit();
        Target group = Target.many(List.of(dummy, d2));

        double before1 = dummy.getHp();
        double before2 = d2.getHp();

        caster.ultimate(group);

        assertTrue(dummy.getHp() < before1);
        assertTrue(d2.getHp() < before2);
    }

    /**
     * Test skill triggers cooldown.
     */
    @Test
    void skillTriggersCooldown() {
        caster.skill(Target.one(dummy));

        assertFalse(caster.canUseSkill());
    }

    /**
     * Test castSkill SKILL branch.
     */
    @Test
    void castSkillSkillBranchWorks() {
        caster.castSkill(SkillType.SKILL, Target.one(dummy));

        assertFalse(caster.canUseSkill());
    }
}