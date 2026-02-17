package Component.Unit;



public class Monster extends Unit{
    public Monster(int atk, int maxHp, int def,Element element) {
        super(atk, maxHp, def,element);
    }

    @Override
    public double takeDamage(double dmg) {
        super.takeDamage(dmg);
    }
    public void attack(Unit unit){
        unit.takeDamage(this.getAtk());
    }

    @Override
    public void scaleStat() {
        //to do
    }
}
