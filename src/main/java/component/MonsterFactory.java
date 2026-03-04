package component;

import java.util.ArrayList;
import java.util.Random;

/**
 * Factory class responsible for spawning enemy monster teams.
 *
 * <p>This factory generates a team of monsters for a given stage.
 * Monster types are selected using weighted random probabilities that
 * change as the stage increases, and each spawned monster is scaled
 * to match the current stage difficulty.</p>
 *
 * <p>Current behavior:</p>
 * <ul>
 *   <li>Spawns a fixed-size team of {@value #TEAM_SIZE} monsters</li>
 *   <li>Each monster type is chosen by {@link #rollType(int, int)}</li>
 *   <li>Each monster is scaled using {@link Monster#scaleToStage(int)}</li>
 *   <li>If all 3 monsters are identical, the last monster may be forced to change type
 *       to reduce repetition</li>
 * </ul>
 */
public class MonsterFactory {

    /** Fixed number of monsters spawned per team. */
    private static final int TEAM_SIZE = 3;

    /** Random number generator for monster type selection. */
    private final Random rng = new Random();

    /** Constructs a MonsterFactory. */
    public MonsterFactory() {}

    /**
     * Spawns a monster team scaled to the specified stage.
     *
     * <p>For each team slot, this method rolls a monster type,
     * creates the monster, scales it to the stage, and adds it to the team.</p>
     *
     * @param stageCounter current stage number (recommended >= 1)
     * @return a list of spawned monsters (size = {@value #TEAM_SIZE})
     */
    public ArrayList<Monster> spawnMonster(int stageCounter) {
        ArrayList<Monster> team = new ArrayList<>();

        for (int i = 0; i < TEAM_SIZE; i++) {
            int type = rollType(stageCounter, i);
            Monster m = new Monster(type);
            m.scaleToStage(stageCounter);
            team.add(m);
        }

        // Prevent the team from being all identical (reduce repetition)
        if (team.get(0).getName().equals(team.get(1).getName())
                && team.get(1).getName().equals(team.get(2).getName())) {

            // Force the last monster to change type (implementation-specific behavior)
            int forced = (rng.nextInt(2) + 1);

            // Note: Ensure monster names/types are consistent with this condition.
            if (team.get(2).getName().equals("Type1")) forced = 2;

            Monster m = new Monster(forced);
            m.scaleToStage(stageCounter);
            team.set(2, m);
        }

        return team;
    }

    /**
     * Rolls (selects) a monster type based on stage-dependent weights.
     *
     * <p>As stage increases, the probability of stronger monster types
     * becomes higher while weaker types become less frequent.</p>
     *
     * @param stage     current stage number
     * @param slotIndex index within the team (currently not used, reserved for future variation)
     * @return monster type identifier (1..3)
     */
    private int rollType(int stage, int slotIndex) {
        int s = Math.max(1, stage);

        // Weight configuration: slime higher early, others increase with stage
        int wSlime = Math.max(20, 60 - s * 4);
        int wCross = Math.min(45, 20 + s * 3);
        int wCabb  = Math.min(45, 20 + s * 3);

        int total = wSlime + wCross + wCabb;
        int r = rng.nextInt(total);

        if (r < wSlime) return 1;
        if (r < wSlime + wCross) return 2;
        return 3;
    }
}