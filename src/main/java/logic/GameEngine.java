package logic;

import component.Element;
import component.Unit.Monster;
import component.Unit.MonsterFactory;
import component.Unit.heroes.Heroes;
import component.Unit.heroes.Archer;
import component.Unit.heroes.Caster;
import component.Unit.heroes.Fighter;
import component.Unit.heroes.Tank;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

public class GameEngine {
    private static final int MAX_TEAM_SIZE = 3;
    private static final int MAX_REROLL = 3;
    public static ArrayList<Heroes> TEAM;
    public static ArrayList<Monster> MONSTER_TEAM;
    public static int STAGE_COUNTER = 0;
    public List<Heroes> AllHero;
    public static Heroes upgradeHero = null;
    private static int COUNT_REROLL=0;
    private static MonsterFactory MONSTER_FACTORY;

    public GameEngine() {
       newGame();
    }
    public void newGame() {
        if (TEAM == null) TEAM = new ArrayList<>();
        else TEAM.clear();

        if (MONSTER_TEAM == null) MONSTER_TEAM = new ArrayList<>();
        else MONSTER_TEAM.clear();

        setStageCounter(1);
        setCountReroll(0);
        setUpgradeHero(null);
        MONSTER_FACTORY= new MonsterFactory();
        this.AllHero = List.of(
                new Caster(),
                new Archer(),
                new Tank(),
                new Fighter()
        );
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

    public static ArrayList<Heroes> getHeroTEAM() {
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
        else{
            TEAM.add(hero);
            return true;
        }
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

    public static int getMaxReroll() {
        return MAX_REROLL;

    }
    public static int getCountReroll() {
        return COUNT_REROLL;
    }

    public static void setCountReroll(int countReroll) {
        COUNT_REROLL = countReroll;
    }
    public static void addCountReroll(int countReroll) {
        COUNT_REROLL +=countReroll;
    }

    public static int getStageCounter() {
        return STAGE_COUNTER;
    }

    public static void setStageCounter(int stageCounter) {
        STAGE_COUNTER = stageCounter;
    }
    public static void addStageCounter(int stageCounter) {
        STAGE_COUNTER += stageCounter;
    }

    public static MonsterFactory getMonsterFactory() {
        return MONSTER_FACTORY;
    }

    public static ArrayList<Monster> getMonsterTeam() {
        return MONSTER_TEAM;
    }

    public static void setMonsterTeam() {
        MONSTER_TEAM = getMonsterFactory().spawnMonster(getStageCounter());
    }


}
