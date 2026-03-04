package component.heroes;

import component.Element;
import component.Target;
import component.Monster;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the Fighter class.
 *
 * This test class verifies:
 * 1) The self-healing effect when using the skill.
 * 2) The damage effect when using the ultimate ability.
 */
class FighterTest {

    /**
     * Test that the Fighter heals himself when using the skill.
     * After calling skill(), the HP should increase from its initial value.
     */
    @Test
    void testHealOnSkill() {
        Fighter f = new Fighter();
        f.setElement(Element.FIRE);
        Monster m = new Monster(1);

        f.setHp(100);
        f.skill(Target.one(m));

        // Fighter should recover HP after using the skill
        assertTrue(f.getHp() > 100);
    }

    /**
     * Test that the ultimate ability deals damage to the target.
     * The monster's HP should decrease after ultimate() is used.
     */
    @Test
    void testUltimateDamage() {
        Fighter f = new Fighter();
        f.setElement(Element.FIRE);
        Monster m = new Monster(1);

        double before = m.getHp();
        f.ultimate(Target.one(m));

        // Monster HP should decrease after taking ultimate damage
        assertTrue(m.getHp() < before);
    }
}