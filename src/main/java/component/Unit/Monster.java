package component.Unit;

import component.Element;

public class Monster extends Unit {
    public Monster(String name,double atk, double maxHp, double def, Element element) {
        super(name,atk, maxHp, def, element);
    }

    public void attack(Unit target) {
        if (target == null || target.isDead()) return;
        target.takeDamage(getAtk());
    }

    @Override
    public void scaleStat(double scale) {
        double frac = (getMaxHp() <= 0) ? 0 : (getHp() / getMaxHp());

        setMaxHp(getMaxHp() * scale);
        setHp(frac * getMaxHp());

        setAtk(getAtk() * scale);
        setDef(getDef() * scale);
    }
}
