package component;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the Monster class.
 *
 * This test class verifies:
 * 1) Base stats initialization for a specific monster type.
 * 2) Stat scaling when progressing to higher stages.
 * 3) Attack functionality and damage output.
 */
class MonsterTest {

    /**
     * Test that a Type1 monster is initialized
     * with the correct base stats.
     */
    @Test
    void testBaseStatsType1() {
        Monster m = new Monster(1);

        // Verify base properties
        assertEquals("Type1", m.getName());
        assertEquals(35, m.getAtk(), 0.0001);
        assertEquals(400, m.getMaxHp(), 0.0001);
    }

    /**
     * Test that monster stats increase when scaling to a higher stage.
     * After scaling to stage 2, both HP and ATK should increase.
     */
    @Test
    void testScaleToStage() {
        Monster m = new Monster(1);
        m.scaleToStage(2);

        // Stats should increase compared to base values
        assertTrue(m.getMaxHp() > 400);
        assertTrue(m.getAtk() > 35);
    }

    /**
     * Test that the attack method deals positive damage.
     * The returned damage value should be greater than zero.
     */
    @Test
    void testAttack() {
        Monster m = new Monster(1);
        m.setElement(Element.FIRE);
        Monster target = new Monster(1);

        double damage = m.attack(target);

        // Damage dealt should be positive
        assertTrue(damage > 0);
    }
}