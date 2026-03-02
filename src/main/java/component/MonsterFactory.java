package component;

import java.util.ArrayList;
import java.util.Random;

public class MonsterFactory {

    private static final int TEAM_SIZE = 3;
    private final Random rng = new Random();

    public MonsterFactory(){}

    public ArrayList<Monster> spawnMonster(int stageCounter){
        ArrayList<Monster> team = new ArrayList<>();

        for (int i = 0; i < TEAM_SIZE; i++) {
            int type = rollType(stageCounter, i);
            Monster m = new Monster(type);
            m.scaleToStage(stageCounter);   // ✅ สเกลด่านโดยไม่แตะ engine
            team.add(m);
        }

        // กัน 3 ตัวซ้ำ (เพื่อให้ไม่จำเจ)
        if (team.get(0).getName().equals(team.get(1).getName())
                && team.get(1).getName().equals(team.get(2).getName())) {
            // บังคับให้ตัวสุดท้ายเปลี่ยนชนิด
            int forced = (rng.nextInt(2) + 1);
            if (team.get(2).getName().equals("Slime")) forced = 2; // เปลี่ยนไป Crossaint
            Monster m = new Monster(forced);
            m.scaleToStage(stageCounter);
            team.set(2, m);
        }

        return team;
    }

    // stage ยิ่งสูง ยิ่งเพิ่มโอกาส Crossaint/Cabbage ให้โหดขึ้น
    private int rollType(int stage, int slotIndex){
        int s = Math.max(1, stage);

        // weight: slime สูงช่วงต้น, crossaint/cabbage สูงขึ้นตามด่าน
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