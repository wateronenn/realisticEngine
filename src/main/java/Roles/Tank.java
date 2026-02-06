package Roles;

import Action.Action;
import Battle.BattleContext;
import Unit.Element;
import Unit.PlayerCharacter;
import Action.TankSkill;
import java.util.List;

public class Tank extends PlayerCharacter {
    private int shieldBuff;
    public Tank(){
        super(15,200,30, Element.LIGHT);
        setShieldBuff(30);
    }
    public Action getSkillAction(BattleContext ctx){
        int shieldAmount = 30;
        return new TankSkill(ctx.party,shieldBuff);
    }
    //to be implemented
    public Action getUltimateAction(BattleContext ctx){
        return null ;
    }

    public void setShieldBuff(int shieldBuff) {
        this.shieldBuff = shieldBuff;
    }

    public int getShieldBuff() {
        return shieldBuff;
    }
}
