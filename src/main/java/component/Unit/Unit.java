package component.Unit;

import component.Element;

public abstract class Unit {
    private String name;
    private double atk;
    private double hp;
    private double def;
    private double maxHp;
    private Element element;



    public Unit(String name,double atk, double maxHp, double def, Element element) {
        setName(name);
        setAtk(atk);
        setMaxHp(maxHp);
        setHp(maxHp);
        setDef(def);
        setElement(element);
    }

    public void setElement(Element element) {
        this.element = element;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public double takeDamage(double dmg) {
        double startHp = hp;
        dmg = dmgReduction(dmg);
        if (dmg <= 0) return 0.0;

        setHp(hp - dmg);
        return startHp - hp;
    }

    public double dmgReduction(double dmg) {
        dmg = Math.max(0, dmg);
        return Math.max(0, dmg - def);
    }

    public double getAtk() {
        return atk;
    }

    public double getDef() {
        return def;
    }

    public boolean isDead() { return hp <= 0; }

    public double getHp() {
        return hp;
    }

    public void setMaxHp(double maxHp) {
        this.maxHp = Math.max(0, maxHp);
        if (hp > this.maxHp) hp = this.maxHp;
    }
    public double getMaxHp() { return maxHp; }

    public void setAtk(double atk) {
        this.atk = Math.max(0, atk);
    }
    public void setDef(double def) {
        this.def = Math.max(0, def);
    }
    public void setHp(double hp) {
        this.hp = Math.max(0, Math.min(hp, maxHp));
    }
    public abstract void scaleStat(double scale);

    public Element getElement() {
        return element;
    }

    public double getHpPercent() {
        return maxHp <= 0 ? 0 : (hp * 100.0 / maxHp);
    }
}

