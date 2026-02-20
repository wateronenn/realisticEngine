package component.Unit.heroes;

import component.Element;
import component.Unit.Buffing;
import component.Unit.Target;
import component.Unit.Unit;

public class Caster extends Heroes implements Buffing {
    public Caster(){
        super("Caster",50,220,5);
        setHeroClass("Caster");
        setActionOrder(3);
    }
    @Override
    public void normalAttack(Unit target) {
        attack(target,effectiveAtk());
    }

    @Override
    public void skill(Target target) {
        Unit t = target.single();
        attack(t,effectiveAtk()*0.6);
        buff(this);
    }

    @Override
    public void ultimate(Target target) {
        var targets = target.many();
        for(Unit unit:targets){
            attack(unit,effectiveAtk()*0.8);
        }
    }

    @Override
    public void buff(Unit target){
        if (target instanceof Heroes h) {
            // +10% ATK for 1 turn (intended)
            h.applyAtkBuff(1.2, 0.0, 1);
        }
    }
}
