package logic;

import component.Monster;
import component.heroes.Heroes;

import java.util.Comparator;
import java.util.List;

/**
 * Data model representing the current state of a battle.
 *
 * <p>This class stores the core battle data used by {@link BattleEngine},
 * including the hero team, monster team, currently active hero, and any
 * pending skill awaiting target selection.
 *
 * <p>The model itself does not contain battle logic. It only stores and
 * exposes battle state. The {@link BattleEngine} is responsible for
 * updating and interpreting this data.
 */
public class BattleModel {

    /** List of heroes participating in the battle, sorted by action order. */
    private List<Heroes> HERO_TEAM = null;

    /** List of monsters participating in the battle. */
    private List<Monster> MONSTER_TEAM = null;

    /** Index of the currently active hero in the hero team list. */
    private int activeHeroIndex = 0;

    /** Skill selected by the player that is waiting for target resolution. */
    private SkillType pendingSkill = null;

    /**
     * Constructs a battle model with the provided hero and monster teams.
     *
     * <p>The hero team will be sorted based on their action order to determine
     * the sequence in which heroes take their turns.
     *
     * @param heroes list of heroes participating in the battle
     * @param monsters list of monsters participating in the battle
     */
    public BattleModel(List<Heroes> heroes, List<Monster> monsters) {
        this.HERO_TEAM = heroes;
        HERO_TEAM.sort(Comparator.comparing(Heroes::getActionOrder));
        this.MONSTER_TEAM = monsters;
    }

    /**
     * Returns the list of heroes participating in the battle.
     *
     * @return list of hero units
     */
    public List<Heroes> getHERO_TEAM() {
        return HERO_TEAM;
    }

    /**
     * Returns the list of monsters participating in the battle.
     *
     * @return list of monster units
     */
    public List<Monster> getMONSTER_TEAM() {
        return MONSTER_TEAM;
    }

    /**
     * Returns the index of the currently active hero.
     *
     * <p>This index corresponds to the hero whose turn is currently being processed.
     *
     * @return index of active hero in the hero team list
     */
    public int getActiveHeroIndex() {
        return activeHeroIndex;
    }

    /**
     * Sets the index of the currently active hero.
     *
     * @param activeHeroIndex index of the hero whose turn is active
     */
    public void setActiveHeroIndex(int activeHeroIndex) {
        this.activeHeroIndex = activeHeroIndex;
    }

    /**
     * Returns the skill currently selected by the player that is waiting
     * for target resolution.
     *
     * <p>This value is typically set after the player chooses a skill and
     * cleared after the action is resolved.
     *
     * @return pending skill type or null if no skill is pending
     */
    public SkillType getPendingSkill() {
        return pendingSkill;
    }

    /**
     * Sets the pending skill selected by the player.
     *
     * <p>This is used when the engine requires additional input such as
     * selecting a target or ally before resolving the action.
     *
     * @param pendingSkill skill type awaiting resolution
     */
    public void setPendingSkill(SkillType pendingSkill) {
        this.pendingSkill = pendingSkill;
    }
}