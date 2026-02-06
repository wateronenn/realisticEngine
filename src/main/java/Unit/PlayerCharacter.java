package Unit;

import Action.Action;
import Battle.BattleContext;



public abstract class PlayerCharacter extends Unit implements ThreatHolder{
    protected int shield=0;
    protected int threat=0;
    public PlayerCharacter(int atk,int maxHp, int def,Element element) {
        super(atk, maxHp, def,element);
        setShield(0);
    }


    @Override
    public int getThreat() {
        return threat;
    }

    @Override
    public void addThreat(int amount) {
        threat =Math.max(0,amount);
    }
    public void resetThreat() {
        threat = 0;
    }
    public void addShield(int amount) {
        if (amount <= 0) return;
        shield += amount;
    }

    @Override
    public void takeDamage(int dmg) {
        dmg = Math.max(0, dmg);
        int absorbed = Math.min(shield, dmg);
        shield -= absorbed;
        dmg -= absorbed;
        super.takeDamage(dmg);
    }
    public void setShield(int shield) {
        this.shield = Math.max(shield,0);
    }

    public int getShield() {
        return shield;
    }


    public abstract Action getSkillAction(BattleContext ctx);
    public abstract Action getUltimateAction(BattleContext ctx);
}
