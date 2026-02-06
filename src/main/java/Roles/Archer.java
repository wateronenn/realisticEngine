package Roles;

import Action.Action;
import Battle.BattleContext;
import Unit.Element;
import Unit.PlayerCharacter;

public class Archer extends PlayerCharacter {
    public Archer(){
        super(40,90,10, Element.LIGHT);
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
