package component;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * UnitTest
 *
 * This class tests all core behaviors of Unit:
 * - Constructor
 * - Damage reduction
 * - HP limits
 * - Attack logic
 * - Death condition
 * - HP percentage
 */
class UnitTest {

    Unit unit;
    Unit target;

    /**
     * DummyUnit is a simple concrete subclass of Unit.
     * We need this because Unit is abstract and cannot be instantiated directly.
     */
    static class DummyUnit extends Unit {
        public DummyUnit(String name, double atk, double hp, double def) {
            super(name, atk, hp, def);
        }
    }

    /**
     * This method runs before each test.
     * It ensures each test starts with a fresh object.
     */
    @BeforeEach
    void setup() {
        unit = new DummyUnit("Tester", 50, 200, 10);
        target = new DummyUnit("Enemy", 40, 150, 5);
    }

    /**
     * Test constructor correctly sets initial values.
     */
    @Test
    void constructorSetsValuesCorrectly() {
        assertEquals("Tester", unit.getName());
        assertEquals(50, unit.getAtk());
        assertEquals(200, unit.getMaxHp());
        assertEquals(200, unit.getHp());
        assertEquals(10, unit.getDef());
    }

    /**
     * Test damage reduction subtracts defense correctly.
     */
    @Test
    void damageReductionWorks() {
        double reduced = unit.dmgReduction(30);
        assertEquals(20, reduced); // 30 - 10 defense
    }

    /**
     * Test damage reduction does not allow negative damage.
     */
    @Test
    void damageCannotBeNegative() {
        double reduced = unit.dmgReduction(-50);
        assertEquals(0, reduced);
    }

    /**
     * Test takeDamage reduces HP properly.
     */
    @Test
    void takeDamageReducesHp() {
        double before = unit.getHp();
        double taken = unit.takeDamage(30);

        assertEquals(20, taken);
        assertEquals(before - 20, unit.getHp());
    }

    /**
     * Test unit is marked dead when HP reaches zero.
     */
    @Test
    void unitDiesWhenHpZero() {
        unit.takeDamage(1000);
        assertTrue(unit.isDead());
    }

    /**
     * Test HP cannot exceed maximum HP.
     */
    @Test
    void hpCannotExceedMax() {
        unit.setHp(999);
        assertEquals(unit.getMaxHp(), unit.getHp());
    }

    /**
     * Test HP cannot go below zero.
     */
    @Test
    void hpCannotGoBelowZero() {
        unit.setHp(-100);
        assertEquals(0, unit.getHp());
    }

    /**
     * Test attack reduces target HP.
     */
    @Test
    void attackDealsDamage() {
        double before = target.getHp();
        unit.attack(target, 50);

        assertTrue(target.getHp() < before);
    }

    /**
     * Test HP percentage calculation.
     */
    @Test
    void hpPercentWorks() {
        unit.setHp(100);
        assertEquals(50, unit.getHpPercent());
    }
}