package component.heroes;

import component.Target;
import org.junit.jupiter.api.Test;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

class TankTest {

    @Test
    void testSkillHeal() {
        Tank t = new Tank();
        Fighter ally = new Fighter();

        ally.setHp(100);
        t.skill(Target.one(ally));

        // Heal 30% of maxHp
        assertTrue(ally.getHp() > 100);
    }

    @Test
    void testUltimateShield() {
        Tank t = new Tank();
        Fighter ally = new Fighter();

        t.ultimate(Target.many(List.of(ally)));

        // Ally should get shield
        assertTrue(ally.getShield() > 0);
    }
}