package component.heroes;

import component.Target;
import component.Unit;
import logic.SkillType;

public class Archer extends Heroes {
    private int bowStack=1;

    public Archer(){

        super("Archer",40,270,5);
        setHeroClass("Archer");
        setActionOrder(4);
        skillCdMax = 0;
        ultCdMax = 0;
    }
    @Override
    public void normalAttack(Unit target) {
        attack(target,effectiveAtk());
    }

    @Override
    public void skill(Target target) {
        this.increaseBowStack();
        triggerSkillCd();
    }

    @Override
    public void ultimate(Target target) {
        var targets = target.many();
        double totaldmg = bowStack * effectiveAtk()*( (10.00 + targets.size()) /10.00);
        for(Unit t:targets){
            double dmgPerUnit = totaldmg/targets.size();
            attack(t,dmgPerUnit);
        }
        triggerUltCd();
        resetBow();
    }
    public void resetBow(){
        bowStack=1;
    }
    public void increaseBowStack(){
        if(bowStack<5) bowStack++;
    }

    public int getBowStack() {
        return bowStack;
    }
    public void castSkill(SkillType type, Target target) {

        switch (type) {

            case NORMAL_ATTACK -> {
                normalAttack(target.single());
            }

            case SKILL -> {

                skill(target);
            }

            case ULTIMATE -> {
                ultimate(target);
            }
        }
    }

}
