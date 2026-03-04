package component;

import logic.SkillType;

/**
 * Abstract base class representing a combat unit in the game.
 *
 * <p>A Unit defines core combat attributes such as attack, defense,
 * health points, and elemental affinity. Both Hero and Monster
 * classes extend this base abstraction.</p>
 *
 * <p>This class is responsible for:</p>
 * <ul>
 *   <li>Damage calculation and defense reduction</li>
 *   <li>Elemental damage modifier application</li>
 *   <li>HP management and death state tracking</li>
 *   <li>Base scaling configuration for heroes and monsters</li>
 * </ul>
 *
 * <p>Subclasses should override skill-related behavior where necessary.</p>
 *
 * @author Puttisan
 * @version 1.0
 */
public abstract class Unit {

    /** Display name of the unit */
    private String name;

    /** Base attack value */
    private double atk;

    /** Current health points */
    private double hp;

    /** Defense value used to reduce incoming damage */
    private double def;

    /** Maximum health points */
    private double maxHp;

    /** Elemental affinity of this unit */
    private Element element;

    /**
     * Constructs a new Unit with base combat stats.
     *
     * @param name   unit name
     * @param atk    attack value (must be >= 0)
     * @param maxHp  maximum HP (must be >= 0)
     * @param def    defense value (must be >= 0)
     */
    public Unit(String name, double atk, double maxHp, double def) {
        setName(name);
        setAtk(atk);
        setMaxHp(maxHp);
        setHp(maxHp);
        setDef(def);
    }

    /**
     * Applies incoming damage to this unit after defense reduction.
     *
     * @param dmg raw incoming damage
     * @return actual damage taken (after reduction)
     */
    public double takeDamage(double dmg) {
        double startHp = hp;
        dmg = dmgReduction(dmg);
        if (dmg <= 0) return 0.0;

        setHp(hp - dmg);
        return startHp - hp;
    }

    /**
     * Reduces incoming damage using defense.
     *
     * @param dmg raw damage
     * @return damage after defense mitigation (never negative)
     */
    public double dmgReduction(double dmg) {
        dmg = Math.max(0, dmg);
        return Math.max(0, dmg - def);
    }

    /**
     * Performs an elemental attack on a target.
     *
     * Final damage = rawDamage × elemental modifier
     *
     * @param target     target unit
     * @param rawDamage  base damage before modifier
     * @return actual damage dealt
     */
    public double attack(Unit target, double rawDamage) {
        if (target == null || target.isDead()) return 0;

        double modifier = this.element.getModifierAgainst(target.element);
        double finalDamage = rawDamage * modifier;
        return target.takeDamage(finalDamage);
    }

    /**
     * Checks whether this unit is dead.
     *
     * @return true if HP less than or equal 0
     */
    public boolean isDead() {
        return hp <= 0;
    }

    /**
     * Returns HP percentage (0–100).
     *
     * @return current HP as percentage of max HP
     */
    public double getHpPercent() {
        return maxHp <= 0 ? 0 : (hp * 100.0 / maxHp);
    }

    /**
     * Casts a skill.
     * Subclasses should override this method to implement skill logic.
     *
     * @param type   skill type
     * @param target skill target
     */
    public void castSkill(SkillType type, Target target) {}

    // ===== Getters & Setters =====

    /** @return unit name */
    public String getName() { return name; }

    /** Sets unit name */
    public void setName(String name) { this.name = name; }

    /** @return attack value */
    public double getAtk() { return atk; }

    /** Sets attack value (must be >= 0) */
    public void setAtk(double atk) {
        this.atk = Math.max(0, atk);
    }

    /** @return defense value */
    public double getDef() { return def; }

    /** Sets defense value (must be >= 0) */
    public void setDef(double def) {
        this.def = Math.max(0, def);
    }

    /** @return current HP */
    public double getHp() { return hp; }

    /**
     * Sets HP, clamped between 0 and maxHp.
     */
    public void setHp(double hp) {
        this.hp = Math.max(0, Math.min(hp, maxHp));
    }

    /** @return maximum HP */
    public double getMaxHp() { return maxHp; }

    /**
     * Sets maximum HP.
     * If current HP exceeds new max HP, it will be reduced.
     */
    public void setMaxHp(double maxHp) {
        this.maxHp = Math.max(0, maxHp);
        if (hp > this.maxHp) hp = this.maxHp;
    }

    /** @return elemental type */
    public Element getElement() { return element; }

    /** Sets elemental type */
    public void setElement(Element element) {
        this.element = element;
    }
}