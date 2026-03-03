package component.heroes;

import component.Buffing;
import component.Target;
import component.Unit;
import logic.SkillType;

/**
 * Caster hero class.
 *
 * <p>The Caster is a high-damage magic attacker specializing in
 * burst damage and self-buffing abilities.</p>
 *
 * <p>Base Stats:
 * <ul>
 *     <li>Attack: 50</li>
 *     <li>Max HP: 260</li>
 *     <li>Defense: 5</li>
 * </ul>
 *
 * <p>Cooldowns:
 * <ul>
 *     <li>Skill: 2 turns</li>
 *     <li>Ultimate: 4 turns</li>
 * </ul>
 *
 * <p>Abilities:
 * <ul>
 *     <li>Skill: Deals reduced damage and buffs self</li>
 *     <li>Ultimate: Deals area damage to multiple targets</li>
 * </ul>
 */
public class Caster extends Heroes implements Buffing {

    /**
     * Constructs a Caster hero with predefined base stats and cooldown values.
     */
    public Caster() {
        super("Caster", 50, 260, 5);
        setHeroClass("Caster");
        setActionOrder(3);
        skillCdMax = 2;
        ultCdMax = 4;
    }

    /**
     * Performs a normal attack using the hero's effective attack value.
     *
     * @param target target unit to attack
     */
    @Override
    public void normalAttack(Unit target) {
        attack(target,effectiveAtk());
    }

    /**
     * Uses the Caster's skill ability.
     *
     * <p>Skill effects:
     * <ul>
     *     <li>Deals 60% of effective attack as damage</li>
     *     <li>Applies a temporary attack buff to self</li>
     * </ul>
     *
     * @param target wrapper containing a single target
     */
    @Override
    public void skill(Target target) {
        Unit t = target.single();
        attack(t,effectiveAtk()*0.6);
        buff(this);
        triggerSkillCd();
    }

    /**
     * Uses the Caster's ultimate ability.
     *
     * <p>Ultimate effect:
     * Deals 80% of effective attack damage to all selected targets.</p>
     *
     * @param target wrapper containing multiple targets
     */
    @Override
    public void ultimate(Target target) {
        var targets = target.many();
        for(Unit unit:targets){
            attack(unit,effectiveAtk()*0.9);
        }
        triggerUltCd();
    }

    /**
     * Applies a temporary attack buff to a hero target.
     *
     * <p>Buff effect:
     * +20% attack multiplier for 1 turn.</p>
     *
     * @param target unit to buff (must be a Heroes instance)
     */
    @Override
    public void buff(Unit target){
        if (target instanceof Heroes h) {
            h.applyAtkBuff(1.2, 0.0, 1);
        }
    }

    /**
     * Dispatches skill execution based on skill type.
     *
     * @param type   type of skill to execute
     * @param target target wrapper containing selected unit(s)
     */
    @Override
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
