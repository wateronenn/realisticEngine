package component.Unit.heroes;

import component.Element;
import component.Unit.Buffing;
import component.Unit.Target;
import component.Unit.Unit;


public class Tank extends Heroes implements Component.Unit.Healing, Buffing {

    public Tank() {
        super("TankyWaterone",20,500,20);
    }

    @Override
    public void normalAttack(Unit target) {
        attack(target, effectiveAtk());
    }


    @Override
    public void skill(Target target) {
        Unit t = target.single();
        if (t == null) return;
        heal(t);
    }

    @Override
    public void heal(Unit target) {
        // heal 50% of target max HP (intended)
        healAmount(target, 0.5 * target.getMaxHp());
    }

    @Override
    public void buff(Unit target) {
        if (target instanceof Heroes h) {
            // +10% ATK for 1 turn (intended)
            h.applyAtkBuff(1.1, 0.0, 1);
        }
    }

    @Override
    public void ultimate(Target target) {
        var targets = target.many();
        if (targets == null) return;

        for (Unit t : targets) {
            if (t instanceof Heroes hTarget) {
                // shield allies based on TANK maxHp (intended)
                hTarget.setShield(0.15 * getMaxHp());
                buff(hTarget);
            }
        }
    }
}
