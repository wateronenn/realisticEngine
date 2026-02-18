package logic;

import component.Unit.Monster;

import java.util.ArrayList;
import java.util.List;

public class MonsterSpawn {
    public List<Monster> spawnMonster(){
        List<Monster> newMonster = new ArrayList<Monster>();
        Monster new1 = new Monster();
        Monster new2 = new Monster();
        Monster new3 = new Monster();
        newMonster.add(new1);
        newMonster.add(new2);
        newMonster.add(new3);
        return newMonster;
    }
}
