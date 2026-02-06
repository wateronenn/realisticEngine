package Unit;
import Unit.Element;
public abstract class Unit implements  Targetable{
    protected int atk;
    protected int hp;
    protected int def;
    protected boolean isDead;
    protected int maxHp;
    protected Element element;

    public Unit(int atk, int maxHp, int def, Element element){
        setAtk(atk);
        setHp(maxHp);
        setDef(def);
        setMaxHp(maxHp);
        setDead(false);
    }


    public int attack(Unit target) {
        int baseDamage = Math.max(0, atk - target.def);
        double modifier = this.element.getModifierAgainst(target.element);
        int finalDamage = (int)(baseDamage * modifier);

        target.takeDamage(finalDamage);
        return finalDamage;
    }
    public void takeDamage(int dmg) {
        dmg = Math.max(0, dmg);
        hp -= dmg;
        if (hp < 0) hp = 0;
    }

    public int getAtk() {
        return atk;
    }

    public int getDef() {
        return def;
    }

    public boolean isDead() {
        return isDead;
    }

    public int getHp() {
        return hp;
    }

    public void setMaxHp(int maxHp) {
        this.maxHp = maxHp;
    }

    public int getMaxHp() {
        return maxHp;
    }

    public void setAtk(int atk) {
        this.atk = Math.max(atk,0);
    }

    public void setDead(boolean dead) {
        isDead = dead;
    }

    public void setDef(int def) {
        this.def = Math.max(def,0);
    }

    public void setHp(int hp) {
        this.hp = Math.max(hp,0);
        if(this.hp==0) setDead(true);
    }

}
