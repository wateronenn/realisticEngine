package component.heroes;

import component.Target;
import org.junit.jupiter.api.Test;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the Tank class.
 *
 * This test class verifies:
 * 1) The healing effect of the Tank's skill.
 * 2) The shield application effect of the Tank's ultimate ability.
 */
class TankTest {

    /**
     * Test that the Tank's skill heals an ally.
     * The ally's HP should increase after the skill is used.
     * The expected behavior is healing for 30% of the ally's max HP.
     */
    @Test
    void testSkillHeal() {
        Tank t = new Tank();
        Fighter ally = new Fighter();

        ally.setHp(100);
        t.skill(Target.one(ally));

        // Ally HP should increase after receiving heal
        assertTrue(ally.getHp() > 100);
    }

    /**
     * Test that the Tank's ultimate grants a shield to allies.
     * After calling ultimate(), the ally should have a positive shield value.
     */
    @Test
    void testUltimateShield() {
        Tank t = new Tank();
        Fighter ally = new Fighter();

        t.ultimate(Target.many(List.of(ally)));

        // Ally should receive a shield from the ultimate ability
        assertTrue(ally.getShield() > 0);
    }
}