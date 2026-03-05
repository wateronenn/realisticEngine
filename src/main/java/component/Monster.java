package component;

/**
 * Represents an enemy monster unit.
 *
 * <p>A Monster is a {@link Unit} with predefined base stats depending on its type,
 * and can be scaled according to the current stage to control difficulty.</p>
 *
 * <p>Supported types (normalized to 1..3):</p>
 * <ul>
 *   <li>1 - Slime (higher HP/DEF scaling)</li>
 *   <li>2 - Crossaint (higher ATK scaling)</li>
 *   <li>3 - Cabbage (balanced scaling)</li>
 * </ul>
 *
 * <p>After construction, base stats are applied automatically via {@link #applyBaseStats()}.
 * Stage scaling can be applied using {@link #scaleToStage(int)}.</p>
 */
public class Monster extends Unit {

    /** Monster type (normalized to 1..3). */
    private final int type;

    /**
     * Constructs a Monster of the given type.
     * The type will be normalized to range 1..3.
     *
     * @param type monster type identifier
     */
    public Monster(int type) {
        super(getNameByType(type), 0, 1, 0);
        this.type = normalizeType(type);
        applyBaseStats();
    }

    /**
     * Normalizes a type value into range 1..3.
     *
     * @param t raw type value
     * @return normalized type in {1,2,3}
     */
    private static int normalizeType(int t) {
        int x = ((t - 1) % 3);
        if (x < 0) x += 3;
        return x + 1;
    }

    /**
     * Returns the monster display name for the given type.
     *
     * @param type raw type value (will be normalized)
     * @return monster name by type
     */
    private static String getNameByType(int type) {
        int t = normalizeType(type);
        return switch (t) {
            case 1 -> "Type1";
            case 2 -> "Type2";
            default -> "Type3";
        };
    }

    /**
     * Applies base stats according to this monster's type.
     * This method sets attack, max HP, current HP, and defense.
     */
    private void applyBaseStats() {
        if (type == 1) {          // Slime
            setAtk(35);
            setMaxHp(400);
            setHp(getMaxHp());
            setDef(10);
        } else if (type == 2) {   // Crossaint
            setAtk(50);
            setMaxHp(280);
            setHp(getMaxHp());
            setDef(6);
        } else {                  // Cabbage
            setAtk(35);
            setMaxHp(350);
            setHp(getMaxHp());
            setDef(12);
        }
    }

    /**
     * Scales this monster's stats to match the specified stage.
     *
     * <p>Stage is clamped to at least 1. Scaling multipliers are type-dependent.</p>
     * <p>After scaling, the monster is healed to full HP (spawn behavior).</p>
     *
     * @param stage current stage number (>= 1)
     */
    public void scaleToStage(int stage) {
        int s = Math.max(1, stage);

        double hpScale, atkScale, defScale;

        if (type == 1) {          // Slime
            hpScale  = Math.pow(1.13, s - 1);
            atkScale = Math.pow(1.12, s - 1);
            defScale = Math.pow(1.06, s - 1);
        } else if (type == 2) {   // Crossaint
            hpScale  = Math.pow(1.10, s - 1);
            atkScale = Math.pow(1.13, s - 1);
            defScale = Math.pow(1.06, s - 1);
        } else {                  // Cabbage
            hpScale  = Math.pow(1.12, s - 1);
            atkScale = Math.pow(1.12, s - 1);
            defScale = Math.pow(1.07, s - 1);
        }


        setMaxHp(Math.round(getMaxHp() * hpScale));
        setHp(getMaxHp());
        setAtk(Math.round(getAtk() * atkScale));
        setDef(Math.round(getDef() * defScale));
    }

    /**
     * Performs a basic monster attack using its current attack value.
     *
     * @param target target unit to attack
     * @return actual damage dealt
     */
    public double attack(Unit target) {
        return super.attack(target, getAtk());
    }
}