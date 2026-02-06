package Unit;

public abstract class PlayerCharacter extends Unit{
    private int shield=0;

    public PlayerCharacter(int atk, int hp, int def) {
        super(atk, hp, def);
        setShield(0);
    }

    public void setShield(int shield) {
        this.shield = Math.max(shield,0);
    }

    public int getShield() {
        return shield;
    }
    // To be implemented
    // element interaction implementation
    // turn counting implementation
    public int attack(Unit target){
        int startHp =  target.getHp();
        target.setHp(target.getHp()-this.getAtk());
        return startHp - target.getHp();
    }

    abstract boolean skill();

    abstract boolean ultimate();
}
