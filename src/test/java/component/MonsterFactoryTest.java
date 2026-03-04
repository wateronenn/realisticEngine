package component;

import org.junit.jupiter.api.Test;
import java.util.ArrayList;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the MonsterFactory class.
 *
 * This test class verifies:
 * 1) The number of monsters spawned per stage.
 * 2) That the factory prevents spawning three identical monsters.
 * 3) That monster stats scale with higher stages.
 */
class MonsterFactoryTest {

    /**
     * Test that spawnMonster() always creates exactly 3 monsters.
     */
    @Test
    void testSpawnSize() {
        MonsterFactory factory = new MonsterFactory();
        ArrayList<Monster> team = factory.spawnMonster(1);

        // Factory should always spawn a team of 3 monsters
        assertEquals(3, team.size());
    }

    /**
     * Test that the factory does not spawn three identical monsters.
     * All three monsters should not have the same name.
     */
    @Test
    void testNoTripleDuplicate() {
        MonsterFactory factory = new MonsterFactory();
        ArrayList<Monster> team = factory.spawnMonster(1);

        boolean triple =
                team.get(0).getName().equals(team.get(1).getName())
                        && team.get(1).getName().equals(team.get(2).getName());

        // There should not be three identical monsters in the team
        assertFalse(triple);
    }

    /**
     * Test that monster stats scale with stage progression.
     * Monsters spawned at higher stages should have equal or higher max HP.
     */
    @Test
    void testScalingIncrease() {
        MonsterFactory factory = new MonsterFactory();
        ArrayList<Monster> stage1 = factory.spawnMonster(1);
        ArrayList<Monster> stage5 = factory.spawnMonster(5);

        // Stage 5 monsters should be stronger (higher or equal max HP)
        assertTrue(stage5.get(0).getMaxHp() >= stage1.get(0).getMaxHp());
    }
}