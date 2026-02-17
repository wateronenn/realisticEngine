package component.Unit.heroes;

import component.Element;
import component.Unit.Target;
import component.Unit.Unit;

public class Archer extends Heroes {
    private int bowStack=1;
    public Archer(){

        super("Archer",50,220,5);
        setHeroClass("Archer");
    }
    @Override
    public void normalAttack(Unit target) {
        attack(target,effectiveAtk());
    }

    @Override
    public void skill(Target target) {
        this.increaseBowStack();
    }

    @Override
    public void ultimate(Target target) {
        var targets = target.many();
        double totaldmg = bowStack * effectiveAtk()*0.7 + (100-getHpPercent())*0.6;
        for(Unit t:targets){
            double dmgPerUnit = totaldmg/targets.size();
            t.takeDamage(dmgPerUnit);
        }
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


}
