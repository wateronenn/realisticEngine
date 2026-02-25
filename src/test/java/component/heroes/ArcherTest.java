package component.heroes;

import component.Target;
import component.Unit;
import logic.SkillType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * ArcherTest
 *
 * This class tests every public behavior of Archer.
 * It verifies:
 * - Constructor initialization
 * - Normal attack
 * - Skill (stack system)
 * - Ultimate (AOE damage)
 * - Stack limit
 * - castSkill method
 */
class ArcherTest {

    Archer archer;   // Archer instance to test
    Unit dummy;      // Dummy target for testing attacks

    /**
     * Dummy concrete Unit class
     * We need this because Unit is abstract.
     */
    static class DummyUnit extends Unit {
        public DummyUnit() {
            super("Dummy", 10, 200, 5);
        }
    }

    /**
     * This method runs before every test.
     * It resets Archer and Dummy to clean state.
     */
    @BeforeEach
    void setup() {
        archer = new Archer();
        dummy = new DummyUnit();
    }

    /**
     * Test constructor initialization.
     * Ensures hero class and initial bow stack are correct.
     */
    @Test
    void constructorSetsCorrectValues() {
        assertEquals("Archer", archer.getHeroClass());
        assertEquals(1, archer.getBowStack());
    }

    /**
     * Test normal attack reduces enemy HP.
     * We compare HP before and after attack.
     */
    @Test
    void normalAttackDealsDamage() {
        double before = dummy.getHp();

        archer.normalAttack(dummy);

        assertTrue(dummy.getHp() < before);
    }

    /**
     * Test skill increases bow stack by 1.
     */
    @Test
    void skillIncreasesBowStack() {
        archer.skill(Target.one(dummy));

        assertEquals(2, archer.getBowStack());
    }

    /**
     * Test bow stack cannot exceed maximum (5).
     */
    @Test
    void bowStackMaxIsFive() {
        for(int i=0;i<10;i++) {
            archer.increaseBowStack();
        }

        assertEquals(5, archer.getBowStack());
    }

    /**
     * Test ultimate damages all targets in group.
     * We verify each target HP decreases.
     */
    @Test
    void ultimateDealsDamageToAllTargets() {
        Unit d2 = new DummyUnit();

        Target group = Target.many(List.of(dummy,d2));

        double before1 = dummy.getHp();
        double before2 = d2.getHp();

        archer.ultimate(group);

        assertTrue(dummy.getHp() < before1);
        assertTrue(d2.getHp() < before2);
    }

    /**
     * Test castSkill with NORMAL_ATTACK.
     * Ensures switch-case correctly calls normalAttack.
     */
    @Test
    void castSkillNormalAttack() {
        double before = dummy.getHp();

        archer.castSkill(SkillType.NORMAL_ATTACK, Target.one(dummy));

        assertTrue(dummy.getHp() < before);
    }

    /**
     * Test resetBow resets stack back to 1.
     */
    @Test
    void resetBowWorks() {
        archer.increaseBowStack();
        archer.resetBow();

        assertEquals(1, archer.getBowStack());
    }
}