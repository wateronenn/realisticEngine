package logic;

import component.Monster;
import component.heroes.Archer;
import component.heroes.Fighter;
import org.junit.jupiter.api.Test;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the BattleModel class.
 *
 * This test class verifies:
 * 1) Hero team sorting based on action order.
 * 2) Proper setting and retrieval of the pending skill.
 */
class BattleModelTest {

    /**
     * Test that heroes are sorted correctly by action order
     * when initializing the BattleModel.
     *
     * Expected order example:
     * Tank(1), Fighter(2), Caster(3), Archer(4)
     */
    @Test
    void testHeroSortedByActionOrder() {
        Archer a = new Archer();
        Fighter f = new Fighter();

        BattleModel model = new BattleModel(
                List.of(a, f),
                List.of(new Monster(1)));

        // Heroes should be sorted in ascending order of actionOrder
        assertTrue(model.getHERO_TEAM().get(0).getActionOrder()
                <= model.getHERO_TEAM().get(1).getActionOrder());
    }

    /**
     * Test that setPendingSkill() correctly updates
     * the pending skill state in the model.
     */
    @Test
    void testSetPendingSkill() {
        Archer a = new Archer();
        BattleModel model = new BattleModel(
                List.of(a),
                List.of(new Monster(1)));

        model.setPendingSkill(SkillType.SKILL);

        // Pending skill should match the value set
        assertEquals(SkillType.SKILL, model.getPendingSkill());
    }
}