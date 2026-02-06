package Unit;

public abstract class Unit {
    private int atk;
    private int hp;
    private int def;
    private boolean isDead;
    public static enum elements{fire,water,nature,dark,light}

    public Unit(int atk,int hp,int def){
        setAtk(atk);
        setHp(hp);
        setDef(def);
        setDead(false);
    }
    abstract int attack(Unit target);


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
