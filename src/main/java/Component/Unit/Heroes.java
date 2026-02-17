package Component.Unit;


public abstract class Heroes extends Unit {
    private double shield=0;
    private int aggro =0;
    public Heroes(double atk, double maxHp, double def, Element element) {
        super(atk, maxHp, def,element);
        setShield(0);
        setAggro(0);

    }

    public void setAggro(int aggro) {
        this.aggro = aggro;
    }

    public int getAggro() {
        return aggro;
    }

    @Override
    public double takeDamage(double dmg) {
        double dmgDone=0;
        dmg = Math.max(0, dmg);
        double absorbed = Math.min(shield, dmg);
        shield -= absorbed;
        dmg -= absorbed;
        dmgDone = absorbed;
        double dmgSurplus = this.takeDamage(dmg);
        return dmgSurplus+dmgDone; //totalDmgtaken;



    }
    public void setShield(int shield) {
        this.shield = Math.max(shield,0);
    }

    public double getShield() {
        return shield;
    }

    @Override
    public void scaleStat() {
        double cHpPc=getHpPercent();
        setMaxHp(getMaxHp()*1.15);
        setHp(cHpPc*getMaxHp());
        setAtk(getAtk()*1.15);
        setDef(getDef()*1.12);
    }
}