package component;

public class Monster extends Unit {
    public Monster(int order) {
        super("Type"+((order%3)+1),35, 320, 8);
    }

    public void attack(Unit target) {
        attack(target, getAtk());
    }



}
