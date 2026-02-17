package component.Unit;

import component.Element;

public class Monster extends Unit {
    public Monster() {
        super("Monster",35, 320, 8);
    }

    public void attack(Unit target) {
        attack(target, getAtk());
    }


    @Override
    public void upgradeHero(double scale) {
        double frac = (getMaxHp() <= 0) ? 0 : (getHp() / getMaxHp());

        setMaxHp(getMaxHp() * scale);
        setHp(frac * getMaxHp());

        setAtk(getAtk() * scale);
        setDef(getDef() * scale);
    }
}
