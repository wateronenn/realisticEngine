package component.Unit.heroes;

import component.Element;
import component.Unit.Target;
import component.Unit.Unit;

public class Fighter extends Heroes implements Component.Unit.Healing {
    public Fighter(){
        super("Fighter",20,250,20);
    }

    @Override
    public void normalAttack(Unit target) {
        return;
    }
    ;

    @Override
    public void ultimate(Target target) {
        return;
    }

    @Override
    public void skill(Target target) {
        return;
    }

    @Override
    public void heal(Unit target) {
        return;
    }
}
