package component.heroes;

import component.Element;
import component.Target;
import component.Monster;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class CasterTest {

    @Test
    void testSkillAppliesBuff() {
        Caster c = new Caster();
        c.setElement(Element.FIRE);
        Monster m = new Monster(1);

        c.skill(Target.one(m));

        // Buff increases atk multiplier
        assertTrue(c.getAtkMul() >= 1.2);
    }

    @Test
    void testSkillCooldownTrigger() {
        Caster c = new Caster();
        c.setElement(Element.FIRE);
        Monster m = new Monster(1);

        c.skill(Target.one(m));

        // Skill cooldown should trigger
        assertFalse(c.canUseSkill());
    }
}