package component.heroes;

import component.Healing;
import component.Target;
import component.Unit;
import logic.SkillType;

public class Fighter extends Heroes implements Healing {
    public Fighter(){

        super("Fighter",40,320,15);
        setHeroClass("Fighter");
        setActionOrder(2);
        skillCdMax = 2;
        ultCdMax = 4;
    }

    @Override
    public void normalAttack(Unit target) {
        attack(target,effectiveAtk());
    }


    @Override
    public void ultimate(Target target) {
        Unit t = target.single();
        attack(t,0.4*t.getMaxHp());
        triggerUltCd();
    }

    @Override
    public void skill(Target target) {
        Unit t = target.single();
        attack(t,effectiveAtk());
        heal(this);
        triggerSkillCd();
        return;
    }

    @Override
    public void heal(Unit target) {
        // heal 80% of this atk  (intended)
        healAmount(target, 0.8 * this.getAtk());
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
