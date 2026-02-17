package component.Unit.heroes;

import component.Element;

public abstract class Caster extends Heroes {
    public Caster(){
        super("Caster",1,1,1, Element.FIRE);
    }
}
