package component;

public class Monster extends Unit {

    private final int type; // 1 slime, 2 crossaint, 3 cabbage

    public Monster(int type) {
        super(getNameByType(type), 0, 1, 0); // ค่อย set จริงด้านล่าง
        this.type = normalizeType(type);

        applyBaseStats();
    }

    private static int normalizeType(int t){
        // กันหลุด: ถ้า t ไม่ใช่ 1..3 ให้แมปกลับเข้า 1..3
        int x = ((t - 1) % 3);
        if (x < 0) x += 3;
        return x + 1;
    }

    private static String getNameByType(int type){
        int t = normalizeStatic(type);
        return switch (t) {
            case 1 -> "Type1";
            case 2 -> "Type2";
            default -> "Type3";
        };
    }

    private static int normalizeStatic(int t){
        int x = ((t - 1) % 3);
        if (x < 0) x += 3;
        return x + 1;
    }

    private void applyBaseStats() {
        if (type == 1) {          // Slime
            setAtk(32);
            setMaxHp(420);
            setHp(getMaxHp());
            setDef(10);
        } else if (type == 2) {   // Crossaint
            setAtk(42);
            setMaxHp(280);
            setHp(getMaxHp());
            setDef(6);
        } else {                  // Cabbage
            setAtk(33);
            setMaxHp(340);
            setHp(getMaxHp());
            setDef(12);
        }
    }

    // เรียกจาก MonsterFactory ได้เลย โดยไม่แตะ engine
    public void scaleToStage(int stage) {
        int s = Math.max(1, stage);

        // สเกลแบบ “คุมความยาก” แยกตามชนิด
        double hpScale, atkScale, defScale;

        if (type == 1) {          // Slime: โตด้าน HP/DEF
            hpScale  = Math.pow(1.10, s - 1);
            atkScale = Math.pow(1.06, s - 1);
            defScale = Math.pow(1.04, s - 1);
        } else if (type == 2) {   // Crossaint: โตด้าน ATK
            hpScale  = Math.pow(1.07, s - 1);
            atkScale = Math.pow(1.11, s - 1);
            defScale = Math.pow(1.03, s - 1);
        } else {                  // Cabbage: สมดุล
            hpScale  = Math.pow(1.09, s - 1);
            atkScale = Math.pow(1.09, s - 1);
            defScale = Math.pow(1.05, s - 1);
        }

        setMaxHp(Math.round(getMaxHp() * hpScale));
        setHp(getMaxHp()); // เติมเต็มตอน spawn (จะได้ไม่เกิดตัวเลือดครึ่ง)
        setAtk(Math.round(getAtk() * atkScale));
        setDef(Math.round(getDef() * defScale));
    }


    public double attack(Unit target) {
        return super.attack(target, getAtk());
    }
}