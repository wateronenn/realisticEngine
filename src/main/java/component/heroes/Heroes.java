package component.heroes;

import component.Target;
import component.Unit;

/**
 * Abstract base class for all playable heroes.
 *
 * <p>Heroes extend {@link Unit} with additional RPG mechanics such as:</p>
 * <ul>
 *   <li>Shield that absorbs incoming damage before HP is reduced</li>
 *   <li>Temporary attack buffs (multiplier + flat bonus) with duration</li>
 *   <li>Skill and ultimate cooldown management</li>
 *   <li>Progression mechanics (upgrade/scale) and post-stage reset</li>
 * </ul>
 *
 * <p>Concrete hero classes must implement combat actions:
 * {@link #normalAttack(Unit)}, {@link #skill(Target)}, and {@link #ultimate(Target)}.</p>
 */
public abstract class Heroes extends Unit {

    /** Current shield value. Shield is consumed before HP damage is applied. */
    private double shield = 0;

    /** Attack multiplier buff (>= 1.0). */
    private double atkMul = 1.0;

    /** Flat attack bonus buff (>= 0.0). */
    private double atkFlat = 0.0;

    /** Remaining turns for the current attack buff (>= 0). */
    private int atkBuffTurns = 0;

    /** Hero class label (e.g., "Archer", "Tank"). */
    private String heroClass;

    /** Turn order priority or sequence position used by the battle system. */
    protected Integer actionOrder;

    /** Maximum cooldown value for skill. */
    protected int skillCdMax;

    /** Remaining cooldown turns for skill (0 means usable). */
    protected int skillCdRemain;

    /** Maximum cooldown value for ultimate. */
    protected int ultCdMax;

    /** Remaining cooldown turns for ultimate (0 means usable). */
    protected int ultCdRemain;

    /**
     * Constructs a hero with base combat stats.
     *
     * @param name  hero name
     * @param atk   base attack (must be >= 0)
     * @param maxHp maximum HP (must be >= 0)
     * @param def   defense (must be >= 0)
     */
    public Heroes(String name, double atk, double maxHp, double def) {
        super(name, atk, maxHp, def);
    }

    /**
     * Sets the action order used by the battle system.
     *
     * @param actionOrder action order value (higher/lower meaning depends on your design)
     */
    protected void setActionOrder(Integer actionOrder) {
        this.actionOrder = actionOrder;
    }

    /**
     * Returns the action order of this hero.
     *
     * @return action order value
     */
    public Integer getActionOrder() {
        return actionOrder;
    }

    /**
     * Sets shield value (clamped to non-negative).
     *
     * @param shield shield value
     */
    public void setShield(double shield) {
        this.shield = Math.max(0.0, shield);
    }

    /**
     * Returns current shield value.
     *
     * @return shield value
     */
    public double getShield() {
        return shield;
    }

    /**
     * Applies damage to this hero, consuming shield first.
     *
     * <p>Damage flow:</p>
     * <ol>
     *   <li>Shield absorbs as much of {@code dmg} as possible</li>
     *   <li>Remaining damage is processed by {@link Unit#takeDamage(double)}
     *       (which includes defense reduction)</li>
     * </ol>
     *
     * @param dmg incoming raw damage
     * @return total effective damage removed from (shield + HP)
     */
    @Override
    public double takeDamage(double dmg) {
        dmg = Math.max(0, dmg);

        double absorbed = Math.min(shield, dmg);
        shield -= absorbed;
        dmg -= absorbed;

        double hpLoss = super.takeDamage(dmg);
        return absorbed + hpLoss;
    }

    /**
     * Applies a temporary attack buff.
     *
     * <p>Rules:</p>
     * <ul>
     *   <li>{@code mul} is clamped to at least 1.0</li>
     *   <li>{@code flat} is clamped to at least 0.0</li>
     *   <li>{@code turns} is clamped to at least 0</li>
     * </ul>
     *
     * @param mul   attack multiplier (>= 1.0)
     * @param flat  flat attack bonus (>= 0.0)
     * @param turns buff duration in turns (>= 0)
     */
    public void applyAtkBuff(double mul, double flat, int turns) {
        this.atkMul = Math.max(1.0, mul);
        this.atkFlat = Math.max(0.0, flat);
        this.atkBuffTurns = Math.max(0, turns);
    }

    /**
     * Heals the target by the given amount (clamped to non-negative),
     * without exceeding the target's maximum HP.
     *
     * @param target unit to heal
     * @param amount healing amount (>= 0)
     * @return actual HP restored
     */
    protected double healAmount(Unit target, double amount) {
        if (target == null) return 0.0;
        amount = Math.max(0.0, amount);
        double start = target.getHp();
        target.setHp(start + amount);
        return target.getHp() - start;
    }

    /**
     * Computes effective attack value including current buffs.
     *
     * @return effective attack = baseAtk * atkMul + atkFlat
     */
    public double effectiveAtk() {
        return super.getAtk() * atkMul + atkFlat;
    }

    /**
     * Performs the hero's normal/basic attack.
     *
     * @param target target unit
     */
    public abstract void normalAttack(Unit target);

    /**
     * Uses the hero's skill action.
     *
     * @param target skill target wrapper
     */
    public abstract void skill(Target target);

    /**
     * Uses the hero's ultimate action.
     *
     * @param target ultimate target wrapper
     */
    public abstract void ultimate(Target target);

    /**
     * Upgrades hero base stats by fixed increments.
     *
     * <p>HP is updated while keeping the same HP percentage as before the upgrade.</p>
     */
    public void upgrade() {
        double frac = (getMaxHp() <= 0) ? 0 : (getHp() / getMaxHp());

        setMaxHp(getMaxHp() + 25);
        setHp(frac * getMaxHp());

        setAtk(getAtk()+4);
        setDef(getDef()+1);
    }

    /**
     * Scales hero stats multiplicatively.
     *
     * <p>HP is updated while keeping the same HP percentage as before scaling.</p>
     */
    public void scale() {
        double frac = getMaxHp() <= 0 ? 0 : getHp() / getMaxHp();
        setMaxHp(getMaxHp() * 1.1);
        setHp(frac * getMaxHp());
        setAtk(getAtk() * 1.1);
        setDef(getDef() + 1);
    }

    /**
     * Sets hero class label.
     *
     * @param heroClass class label
     */
    public void setHeroClass(String heroClass) {
        this.heroClass = heroClass;
    }

    /**
     * Returns hero class label.
     *
     * @return hero class label
     */
    public String getHeroClass() {
        return heroClass;
    }

    /** @param atkMul attack multiplier value */
    public void setAtkMul(double atkMul) {
        this.atkMul = atkMul;
    }

    /** @param atkBuffTurns buff remaining turns */
    public void setAtkBuffTurns(int atkBuffTurns) {
        this.atkBuffTurns = atkBuffTurns;
    }

    /** @param atkFlat flat attack bonus */
    public void setAtkFlat(double atkFlat) {
        this.atkFlat = atkFlat;
    }

    /** @return flat attack bonus */
    public double getAtkFlat() {
        return atkFlat;
    }

    /** @return attack multiplier */
    public double getAtkMul() {
        return atkMul;
    }

    /** @return remaining buff turns */
    public int getAtkBuffTurns() {
        return atkBuffTurns;
    }

    /**
     * Checks if skill is usable (cooldown is 0).
     *
     * @return true if skillCdRemain == 0
     */
    public boolean canUseSkill() {
        return skillCdRemain == 0;
    }

    /**
     * Checks if ultimate is usable (cooldown is 0).
     *
     * @return true if ultCdRemain == 0
     */
    public boolean canUseUlt() {
        return ultCdRemain == 0;
    }

    /** Starts/refreshes skill cooldown to its max value. */
    public void triggerSkillCd() {
        skillCdRemain = skillCdMax;
    }

    /** Starts/refreshes ultimate cooldown to its max value. */
    public void triggerUltCd() {
        ultCdRemain = ultCdMax;
    }

    /** Decreases cooldown counters by 1 turn, if they are greater than 0. */
    public void tickCooldowns() {
        if (skillCdRemain > 0) skillCdRemain--;
        if (ultCdRemain > 0) ultCdRemain--;
    }

    /** Resets both skill and ultimate cooldowns to 0 (usable). */
    public void resetAllCooldowns() {
        skillCdRemain = 0;
        ultCdRemain = 0;
    }

    /** @return remaining skill cooldown */
    public int getSkillCdRemain() {
        return skillCdRemain;
    }

    /** @return remaining ultimate cooldown */
    public int getUltCdRemain() {
        return ultCdRemain;
    }

    /**
     * Resets hero state after a stage/turn.
     *
     * <p>Current behavior:</p>
     * <ul>
     *   <li>If dead: revive to 70% max HP</li>
     *   <li>Else: heal to full</li>
     *   <li>Reset cooldowns and temporary buffs</li>
     *   <li>Reset shield</li>
     *   <li>Apply {@link #scale()} for progression</li>
     * </ul>
     */
    public void resetAfterTurn() {
        if (isDead()) {
            setHp(0.7 * getMaxHp());
        } else {
            setHp(getMaxHp());
        }
        this.resetAllCooldowns();

        setShield(0);
        setAtkFlat(0);
        setAtkMul(1.0);
        setAtkBuffTurns(0);

        scale();
    }

}
