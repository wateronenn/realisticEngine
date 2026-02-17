package component.Unit.heroes;

import component.Element;
import component.Unit.Buffing;
import component.Unit.Target;
import component.Unit.Unit;

public class Caster extends Heroes implements Buffing {
    public Caster(){
        super("Caster",35,200,5);
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
    public void buff(Unit unit){
        return;
    }
}
