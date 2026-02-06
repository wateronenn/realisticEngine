package Action;

import Unit.PlayerCharacter;

import java.util.List;

public class TankSkill implements Action{
    private List<PlayerCharacter> party;
    private int shieldAmount;

    public TankSkill(List<PlayerCharacter> party,int shieldAmount){
        setParty(party);
        setShieldAmount(shieldAmount);
    }

    @Override
    public void execute() {
        for (PlayerCharacter pc : party) {
            if (!pc.isDead()) {
                pc.addShield(shieldAmount);
            }
        }
    }

    public void setParty(List<PlayerCharacter> party) {
        this.party = party;
    }

    public void setShieldAmount(int shieldAmount) {
        this.shieldAmount = shieldAmount;
    }
}
