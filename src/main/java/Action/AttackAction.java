package Action;


import Unit.PlayerCharacter;
import Unit.Unit;

public class AttackAction implements Action{
    private Unit attacker;
    private Unit target;

    public AttackAction(Unit attacker,Unit target){
        setAttacker(attacker);
        setTarget(target);
    }

    @Override
    public void execute() {
        if(attacker.isDead() || target.isDead()) return;
        attacker.attack(target);

        if(target instanceof PlayerCharacter pc){
            pc.addThreat(10);
        }
    }

    public void setAttacker(Unit attacker) {
        this.attacker = attacker;
    }

    public void setTarget(Unit target) {
        this.target = target;
    }

    public Unit getAttacker() {
        return attacker;
    }

    public Unit getTarget() {
        return target;
    }
}
