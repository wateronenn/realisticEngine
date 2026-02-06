package Unit;

import Action.Action;
import Action.AttackAction;
import Battle.BattleContext;

import java.util.List;

public class Monster extends Unit{
    public Monster(int atk, int maxHp, int def,Element element) {
        super(atk, maxHp, def,element);
    }

    public Action chooseAction(BattleContext ctx){
        PlayerCharacter target = ctx.getHighestThreatPlayer();
        return new AttackAction(this,target);

    }
}
