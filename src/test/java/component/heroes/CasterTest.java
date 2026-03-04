package component.heroes;

import component.Element;
import component.Target;
import component.Monster;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the Caster class.
 *
 * This test class verifies that the Caster's skill:
 * 1) Correctly applies an attack multiplier buff.
 * 2) Properly triggers the skill cooldown mechanism.
 */
class CasterTest {

    /**
     * Test that using the skill applies a buff to the caster.
     * The attack multiplier should increase after the skill is used.
     */
    @Test
    void testSkillAppliesBuff() {
        Caster c = new Caster();
        c.setElement(Element.FIRE);
        Monster m = new Monster(1);

        c.skill(Target.one(m));

        // After using the skill, attack multiplier should increase (>= 1.2)
        assertTrue(c.getAtkMul() >= 1.2);
    }

    /**
     * Test that using the skill triggers cooldown.
     * After the skill is used, it should not be immediately reusable.
     */
    @Test
    void testSkillCooldownTrigger() {
        Caster c = new Caster();
        c.setElement(Element.FIRE);
        Monster m = new Monster(1);

        c.skill(Target.one(m));

        // Skill should enter cooldown state
        assertFalse(c.canUseSkill());
    }
}