package component.heroes;

import component.Buffing;
import component.Healing;
import component.Target;
import component.Unit;
import logic.SkillType;

/**
 * Tank hero class.
 *
 * <p>The Tank is a defensive-support hero specializing in:
 * <ul>
 *     <li>Healing a single ally</li>
 *     <li>Providing temporary attack buffs</li>
 *     <li>Granting shields to multiple allies</li>
 * </ul>
 *
 * <p>Base Stats:
 * <ul>
 *     <li>Attack: 20</li>
 *     <li>Max HP: 480</li>
 *     <li>Defense: 15</li>
 * </ul>
 *
 * <p>Cooldowns:
 * <ul>
 *     <li>Skill: 3 turns</li>
 *     <li>Ultimate: 5 turns</li>
 * </ul>
 */
public class Tank extends Heroes implements Healing, Buffing {

    /**
     * Constructs a Tank hero with predefined base stats and cooldown values.
     */
    public Tank() {
        super("Tank", 20, 480, 15);
        setHeroClass("Tank");
        setActionOrder(1);
        skillCdMax = 3;
        ultCdMax = 5;
    }

    /**
     * Performs a normal attack using the hero's effective attack value.
     *
     * @param target target unit to attack
     */
    @Override
    public void normalAttack(Unit target) {
        attack(target, effectiveAtk());
    }

    /**
     * Uses the Tank's skill ability.
     *
     * <p>Skill effect:
     * Heals a single selected target for 30% of its maximum HP.
     * </p>
     *
     * @param target wrapper containing the selected unit
     */
    @Override
    public void skill(Target target) {
        Unit t = target.single();
        if (t == null) return;
        heal(t);
        triggerSkillCd();
    }

    /**
     * Heals the specified unit.
     *
     * <p>Healing amount = 30% of target's maximum HP.</p>
     *
     * @param target unit to be healed
     */
    @Override
    public void heal(Unit target) {
        healAmount(target, 0.3 * target.getMaxHp());
    }

    /**
     * Applies a temporary attack buff to a hero target.
     *
     * <p>Buff effect:
     * +10% attack multiplier for 1 turn.</p>
     *
     * @param target unit to buff (must be a Heroes instance)
     */
    @Override
    public void buff(Unit target) {
        if (target instanceof Heroes h) {
            h.applyAtkBuff(1.1, 0.0, 1);
        }
    }

    /**
     * Uses the Tank's ultimate ability.
     *
     * <p>Ultimate effects:
     * <ul>
     *     <li>Applies a shield to all hero targets equal to 15% of Tank's max HP</li>
     *     <li>Applies attack buff to each shielded hero</li>
     * </ul>
     *
     * @param target wrapper containing multiple targets
     */
    @Override
    public void ultimate(Target target) {
        var targets = target.many();
        if (targets == null) return;

        for (Unit t : targets) {
            if (t instanceof Heroes hTarget) {
                hTarget.setShield(0.15 * getMaxHp());
                buff(hTarget);
            }
        }
        triggerUltCd();
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