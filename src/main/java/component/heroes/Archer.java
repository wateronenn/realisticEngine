package component.heroes;

import component.Target;
import component.Unit;
import logic.SkillType;

/**
 * Archer hero class.
 *
 * <p>The Archer is a scaling damage dealer that builds up {@code bowStack}
 * to increase ultimate damage. The Archer's ultimate damage also increases
 * when the Archer has lower HP (missing HP bonus).</p>
 *
 * <p>Base Stats:
 * <ul>
 *     <li>Attack: 50</li>
 *     <li>Max HP: 270</li>
 *     <li>Defense: 5</li>
 * </ul>
 *
 * <p>Stacks:
 * <ul>
 *     <li>{@code bowStack} starts at 1</li>
 *     <li>Increases up to 5 via {@link #skill(Target)}</li>
 *     <li>Resets back to 1 after {@link #ultimate(Target)}</li>
 * </ul>
 *
 * <p>Cooldowns:
 * <ul>
 *     <li>Skill: 0 turns</li>
 *     <li>Ultimate: 0 turns</li>
 * </ul>
 */
public class Archer extends Heroes {

    /** Current bow stack (1–5). Higher stacks increase ultimate damage. */
    private int bowStack = 1;

    /**
     * Constructs an Archer hero with predefined base stats and cooldown values.
     */
    public Archer() {
        super("Archer", 40, 270, 5);
        setHeroClass("Archer");
        setActionOrder(4);
        skillCdMax = 0;
        ultCdMax = 0;
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
     * Uses the Archer's skill ability.
     *
     * <p>Skill effect:
     * Increases {@code bowStack} by 1 up to a maximum of 5.</p>
     *
     * @param target wrapper containing targets (not used by this skill)
     */
    @Override
    public void skill(Target target) {
        this.increaseBowStack();
        triggerSkillCd();
    }

    /**
     * Uses the Archer's ultimate ability.
     *
     * <p>Ultimate effect:</p>
     * <ul>
     *     <li>Computes total damage based on {@code bowStack}, effective attack</li>
     *     <li>Splits the total damage evenly among all selected targets</li>
     *     <li>Resets {@code bowStack} back to 1 after execution</li>
     * </ul>
     *
     * <p>Damage formula</p>
     * <pre>
     * totalDamage = bowStack * effectiveAtk()*( (10.00 + targets.size()) /10.00)
     * damagePerTarget = totalDamage / numberOfTargets
     * </pre>
     *
     * @param target wrapper containing multiple targets
     */
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

    /**
     * Resets {@code bowStack} back to 1.
     */
    public void resetBow() {
        bowStack = 1;
    }

    /**
     * Increases {@code bowStack} by 1 up to a maximum of 5.
     */
    public void increaseBowStack() {
        if (bowStack < 5) bowStack++;
    }

    /**
     * Returns current bow stack value.
     *
     * @return bowStack (1–5)
     */
    public int getBowStack() {
        return bowStack;
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