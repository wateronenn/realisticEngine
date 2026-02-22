package logic;

import component.Unit.Monster;
import component.Unit.Unit;
import component.Unit.heroes.Heroes;

import java.util.ArrayList;
import java.util.List;

public class BattleModel {
    private List<Heroes> HERO_TEAM = null;
    private  List<Monster> MONSTER_TEAM =null;
    private int activeHeroIndex = 0;
    private SkillType pendingSkill = null;

    public BattleModel(List<Heroes> heroes, List<Monster> monsters) {
        this.HERO_TEAM = heroes;
        this.MONSTER_TEAM = monsters;
    }

    public List<Heroes> getHERO_TEAM() {
        return HERO_TEAM;
    }

    public List<Monster> getMONSTER_TEAM() {

        return MONSTER_TEAM;
    }

    public int getActiveHeroIndex() {
        return activeHeroIndex;
    }

    public void setActiveHeroIndex(int activeHeroIndex) {
        this.activeHeroIndex = activeHeroIndex;
    }

    public SkillType getPendingSkill() {
        return pendingSkill;
    }

    public void setPendingSkill(SkillType pendingSkill) {
        this.pendingSkill = pendingSkill;
    }
}
