package logic;

import component.Monster;
import component.heroes.Archer;
import component.heroes.Fighter;
import org.junit.jupiter.api.Test;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

class BattleModelTest {

    @Test
    void testHeroSortedByActionOrder() {
        Archer a = new Archer();
        Fighter f = new Fighter();

        BattleModel model = new BattleModel(List.of(a,f), List.of(new Monster(1)));

        // Tank(1), Fighter(2), Caster(3), Archer(4)
        assertTrue(model.getHERO_TEAM().get(0).getActionOrder()
                <= model.getHERO_TEAM().get(1).getActionOrder());
    }

    @Test
    void testSetPendingSkill() {
        Archer a = new Archer();
        BattleModel model = new BattleModel(List.of(a), List.of(new Monster(1)));

        model.setPendingSkill(SkillType.SKILL);
        assertEquals(SkillType.SKILL, model.getPendingSkill());
    }
}