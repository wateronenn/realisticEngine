package component;

public class Monster extends Unit {

    private final int type;

    public Monster(int type) {
        super(getNameByType(type), 0, 1, 0);
        this.type = normalizeType(type);
        applyBaseStats();
    }

    private static int normalizeType(int t) {
        int x = ((t - 1) % 3);
        if (x < 0) x += 3;
        return x + 1;
    }

    private static String getNameByType(int type) {
        int t = normalizeType(type);
        return switch (t) {
            case 1 -> "Type1";
            case 2 -> "Type2";
            default -> "Type3";
        };
    }

    private void applyBaseStats() {
        if (type == 1) {          // Slime
            setAtk(30);
            setMaxHp(370);
            setHp(getMaxHp());
            setDef(8);
        } else if (type == 2) {   // Crossaint
            setAtk(38);
            setMaxHp(270);
            setHp(getMaxHp());
            setDef(5);
        } else {                  // Cabbage
            setAtk(30);
            setMaxHp(320);
            setHp(getMaxHp());
            setDef(10);
        }
    }

    public void scaleToStage(int stage) {
        int s = Math.max(1, stage);

        double hpScale, atkScale, defScale;

        if (type == 1) {          // Slime
            hpScale  = Math.pow(1.13, s - 1);
            atkScale = Math.pow(1.07, s - 1);
            defScale = Math.pow(1.05, s - 1);
        } else if (type == 2) {   // Crossaint
            hpScale  = Math.pow(1.08, s - 1);
            atkScale = Math.pow(1.12, s - 1);
            defScale = Math.pow(1.04, s - 1);
        } else {                  // Cabbage
            hpScale  = Math.pow(1.1, s - 1);
            atkScale = Math.pow(1.1, s - 1);
            defScale = Math.pow(1.06, s - 1);
        }

        setMaxHp(Math.round(getMaxHp() * hpScale));
        setHp(getMaxHp());
        setAtk(Math.round(getAtk() * atkScale));
        setDef(Math.round(getDef() * defScale));
    }

    public double attack(Unit target) {
        return super.attack(target, getAtk());
    }
}