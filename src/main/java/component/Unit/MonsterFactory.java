package component.Unit;

import java.util.ArrayList;

public class MonsterFactory {
    public MonsterFactory(){

    }
    // TODO :create temporary function for spawn monster ( can edit this later)
    public ArrayList<Monster> spawnMonster(int stageCounter){
        ArrayList<Monster> monstersTeam = new ArrayList<>();
        Monster m = new Monster();
        for(int i=0;i<3;i++){
            monstersTeam.add(m);
        }
        return monstersTeam;
    }
}
