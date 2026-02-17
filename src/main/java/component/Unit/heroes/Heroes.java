package component.Unit.heroes;


import component.Element;
import component.Unit.Target;
import component.Unit.Unit;

public abstract class Heroes extends Unit {
    private double shield=0;
    private int aggro =0;
    private double atkMul = 1.0;
    private double atkFlat = 0.0;
    private int atkBuffTurns = 0; // later:
    private String heroClass;
    public Heroes(String name,double atk, double maxHp, double def) {
        super(name,atk, maxHp, def);
    }


    public void setAggro(int aggro) {
        this.aggro = Math.max(0, aggro);
    }

    public int getAggro() { return aggro; }

    public void setShield(double shield) {
        this.shield = Math.max(0.0, shield);
    }

    public double getShield() { return shield; }

    @Override
    public double takeDamage(double dmg) {
        dmg = Math.max(0, dmg);

        double absorbed = Math.min(shield, dmg);
        shield -= absorbed;
        dmg -= absorbed;

        double hpLoss = super.takeDamage(dmg);
        return absorbed + hpLoss;
    }

    public void applyAtkBuff(double mul, double flat, int turns) {
        this.atkMul = Math.max(1.0, mul);
        this.atkFlat = Math.max(0.0, flat);
        this.atkBuffTurns = Math.max(0, turns);
    }
    protected double healAmount(Unit target, double amount) {
        if (target == null) return 0.0;
        amount = Math.max(0.0, amount);
        double start = target.getHp();
        target.setHp(start + amount);
        return target.getHp() - start;
    }

    public double effectiveAtk() {
        return super.getAtk() * atkMul + atkFlat;
    }
    public void onTurnEnd() {
        if (atkBuffTurns > 0) {
            atkBuffTurns--;
            if (atkBuffTurns == 0) {
                atkMul = 1.0;
                atkFlat = 0.0;
            }
        }
    }
    public abstract void normalAttack(Unit target);
    public abstract void skill(Target target);
    public abstract void ultimate(Target target);

    public void upgrade() {
        double frac = (getMaxHp() <= 0) ? 0 : (getHp() / getMaxHp());

        setMaxHp(getMaxHp()+25);
        setHp(frac * getMaxHp());

        setAtk(getAtk()+5);
        setDef(getDef()+1);
    }
    public void scale(){
        double frac = getMaxHp() <= 0 ? 0 : getHp() / getMaxHp();
        setMaxHp(getMaxHp() * 1.1);
        setHp(frac * getMaxHp());
        setAtk(getAtk() * 1.1);
        setDef(getDef() +1);
    }

    public void setHeroClass(String heroClass) {
        this.heroClass = heroClass;
    }

    public String getHeroClass(){
        return heroClass;
    }
}
