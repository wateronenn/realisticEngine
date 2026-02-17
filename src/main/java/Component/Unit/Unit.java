package Component.Unit;

public abstract class Unit {
    private double atk;
    private double hp;
    private double def;
    private boolean isDead;
    private double maxHp;
    private Element element;



    public Unit(double atk, double maxHp, double def, Element element){
        setAtk(atk);
        setHp(maxHp);
        setDef(def);
        setMaxHp(maxHp);
        setDead(false);
        setElement(element);
    }

    public void setElement(Element element) {
        this.element = element;
    }


    public double takeDamage(double dmg) {
        double startHp = getHp();
        dmg = Math.max(0, dmg);
        hp -= dmg;
        if (hp < 0) hp = 0;
        double dmgDone = startHp - getHp();
    }

    public double getAtk() {
        return atk;
    }

    public double getDef() {
        return def;
    }

    public boolean isDead() {
        return isDead;
    }

    public double getHp() {
        return hp;
    }

    public void setMaxHp(double maxHp) {
        this.maxHp = maxHp;
    }

    public double getMaxHp() { return maxHp; }

    public void setAtk(double atk) {
        this.atk = Math.max(atk,0);
    }

    public void setDead(boolean dead) {
        isDead = dead;
    }

    public void setDef(double def) {
        this.def = Math.max(def,0);
    }

    public void setHp(double hp) {
        this.hp = Math.max(hp,0);
        if(this.hp==0) setDead(true);
    }
    public abstract void scaleStat();

    public Element getElement() {
        return element;
    }

    public double getHpPercent(){
        return this.hp *100 / this.maxHp;
    }

}

