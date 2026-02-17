package component.Unit;

import component.Element;

public class Monster extends Unit {
    public Monster() {
        super("Monster",35, 320, 8);
    }

    public void attack(Unit target) {
        attack(target, getAtk());
    }



}
