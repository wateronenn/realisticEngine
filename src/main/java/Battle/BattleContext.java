package Battle;

import Unit.Monster;
import Unit.PlayerCharacter;

import java.util.List;

public class BattleContext {
    public List<PlayerCharacter> party;
    public List<Monster> monsters;

    public PlayerCharacter getHighestThreatPlayer(){
        PlayerCharacter best = null;

        for(PlayerCharacter pc:party){
            if(pc.isDead())continue;
            if(best ==  null||pc.getThreat()> best.getThreat()){
                best=pc;
            }
        }
        return best;
    }
}
