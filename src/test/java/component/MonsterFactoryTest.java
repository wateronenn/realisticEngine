package component;

import org.junit.jupiter.api.Test;
import java.util.ArrayList;
import static org.junit.jupiter.api.Assertions.*;

class MonsterFactoryTest {

    @Test
    void testSpawnSize() {
        MonsterFactory factory = new MonsterFactory();
        ArrayList<Monster> team = factory.spawnMonster(1);

        // Should always spawn 3 monsters
        assertEquals(3, team.size());
    }

    @Test
    void testNoTripleDuplicate() {
        MonsterFactory factory = new MonsterFactory();
        ArrayList<Monster> team = factory.spawnMonster(1);

        boolean triple =
                team.get(0).getName().equals(team.get(1).getName())
                        && team.get(1).getName().equals(team.get(2).getName());

        // Factory prevents 3 same monster
        assertFalse(triple);
    }

    @Test
    void testScalingIncrease() {
        MonsterFactory factory = new MonsterFactory();
        ArrayList<Monster> stage1 = factory.spawnMonster(1);
        ArrayList<Monster> stage5 = factory.spawnMonster(5);

        // Stage 5 monsters should be stronger
        assertTrue(stage5.get(0).getMaxHp() >= stage1.get(0).getMaxHp());
    }
}