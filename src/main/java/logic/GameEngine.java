package logic;

import component.Unit.heroes.Heroes;
import component.Unit.heroes.Archer;
import component.Unit.heroes.Caster;
import component.Unit.heroes.Fighter;
import component.Unit.heroes.Tank;

import java.util.ArrayList;
import java.util.List;

public class GameEngine {
    private static final int MAX_TEAM_SIZE = 3;
    private static final int MAX_REROLL = 3;
    public static ArrayList<Heroes> TEAM;
    public List<Heroes> AllHero;
    public static Heroes upgradeHero = null;

    public GameEngine() {
        this.AllHero = List.of(
                new Caster(),
                new Archer(),
                new Tank(),
                new Fighter()
        );
        TEAM = new ArrayList<>();

    }

    ;

    public void newGame() {
        TEAM.clear();
    }

    public List<Heroes> getAvailableHeroes() {
        return AllHero;
    }

    public static boolean isInTeam(Heroes hero) {
        if (TEAM.contains(hero)) {
            return true;
        }
        return false;
    }

    public static boolean checkFullTeam() {
        if (TEAM.size() == 3) {
            return true;
        }
        return false;
    }

    public static ArrayList<Heroes> getTEAM() {
        return TEAM;
    }

    public static int getTeamSize() {
        return TEAM.size();
    }

    public static boolean toggleTeamMember(Heroes hero) {
        if (isInTeam(hero)) {
            TEAM.remove(hero);
            return true;
        } else if (getTeamSize() >= MAX_TEAM_SIZE) {
            return false;
        }

        TEAM.add(hero);
        return true;

    }

    public static void setUpgradeHero(Heroes hero) {
        upgradeHero = hero;
    }
    public static Heroes getUpgradeHero() {
        return upgradeHero;
    }

    public static void upgradingHero(){
        if(getUpgradeHero() != null){
            upgradeHero.upgrade();
        }
    }

    public int getMaxReroll() {
        return MAX_REROLL;
    }
}
