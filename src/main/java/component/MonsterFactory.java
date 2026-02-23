package component;

import java.util.ArrayList;

public class MonsterFactory {
    public MonsterFactory(){

    }
    // TODO :create temporary function for spawn monster ( can edit this later)
    public ArrayList<Monster> spawnMonster(int stageCounter){
        ArrayList<Monster> monstersTeam = new ArrayList<>();
        for(int i=1;i<=3;i++){
            Monster m = new Monster(i);
            monstersTeam.add(m);
        }
        return monstersTeam;
    }
}
