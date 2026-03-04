package component.heroes;

import component.Healing;
import component.Target;
import component.Unit;
import logic.SkillType;

/**
 * Fighter hero class.
 *
 * <p>The Fighter is an offensive hero specializing in high damage output
 * with limited self-sustain through healing.</p>
 *
 * <p>Base Stats:
 * <ul>
 *     <li>Attack: 40</li>
 *     <li>Max HP: 350</li>
 *     <li>Defense: 9</li>
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
 *     <li>Skill: Deals damage and heals self</li>
 *     <li>Ultimate: Deals damage based on target's maximum HP</li>
 * </ul>
 */
public class Fighter extends Heroes implements Healing {

    /**
     * Constructs a Fighter hero with predefined base stats and cooldown values.
     */
    public Fighter() {
        super("Fighter", 40, 350, 9);
        setHeroClass("Fighter");
        setActionOrder(2);
        skillCdMax = 2;
        ultCdMax = 5;
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
     * Uses the Fighter's ultimate ability.
     *
     * <p>Ultimate effect:
     * Deals damage equal to 40% of the target's maximum HP + there attack.</p>
     *
     * @param target wrapper containing a single target
     */
    @Override
    public void ultimate(Target target) {
        Unit t = target.single();
        attack(t,0.4*t.getMaxHp()+getAtk());
        triggerUltCd();
    }

    /**
     * Uses the Fighter's skill ability.
     *
     * <p>Skill effects:
     * <ul>
     *     <li>Deals normal attack damage</li>
     *     <li>Heals self for 80% of base attack</li>
     * </ul>
     *
     * @param target wrapper containing a single target
     */
    @Override
    public void skill(Target target) {
        Unit t = target.single();
        attack(t,effectiveAtk());
        heal(this);
        triggerSkillCd();
        return;
    }

    /**
     * Heals the specified unit.
     *
     * <p>Healing amount = 80% of this hero's base attack value.</p>
     *
     * @param target unit to be healed
     */
    @Override
    public void heal(Unit target) {
        healAmount(target, 0.8 * this.getAtk());
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
