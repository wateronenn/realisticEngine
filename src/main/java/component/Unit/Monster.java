package component.Unit;

import component.Element;

public class Monster extends Unit {
    public Monster(int order) {
        super("Monster "+ order,35, 320, 8);
    }

    public void attack(Unit target) {
        attack(target, getAtk());
    }



}
