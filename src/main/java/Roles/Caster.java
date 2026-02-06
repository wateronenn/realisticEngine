package Roles;

import Action.Action;
import Battle.BattleContext;
import Unit.Element;
import Unit.PlayerCharacter;

public class Caster extends PlayerCharacter {
    public Caster(){
        super(35,100,10, Element.LIGHT);
    }
    //to be implemented
    public Action getSkillAction(BattleContext ctx){
        return null ;
    }
    //to be implemented
    public Action getUltimateAction(BattleContext ctx){
        return null ;
    }
}
