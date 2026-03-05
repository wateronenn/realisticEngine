package logic;

import component.Monster;
import component.MonsterFactory;
import component.heroes.Heroes;
import component.heroes.Archer;
import component.heroes.Caster;
import component.heroes.Fighter;
import component.heroes.Tank;

import java.util.ArrayList;
import java.util.List;

import static java.util.Collections.shuffle;

/**
 * Global game controller responsible for managing game-wide state outside of battle.
 *
 * <p>This class manages:
 * <ul>
 *   <li>Hero selection and team management</li>
 *   <li>Stage progression and monster spawning</li>
 *   <li>Reroll tracking and upgrade selection</li>
 *   <li>High-level {@link GameState} transitions</li>
 * </ul>
 *
 * <p>Most fields and methods are static, so the class behaves like a shared global
 * game state container. UI screens typically call these methods to update team
 * composition, spawn monsters, and move between game phases.
 */
public class GameEngine {

    /** Maximum number of heroes allowed in the player's team. */
    private static final int MAX_TEAM_SIZE = 3;

    /** Maximum number of rerolls allowed during hero selection or related UI. */
    private static final int MAX_REROLL = 3;

    /** Current selected hero team for the player. */
    private static ArrayList<Heroes> TEAM;

    /** Current monster team for the current stage. */
    private static ArrayList<Monster> MONSTER_TEAM;

    /** Current stage number (used for difficulty progression and monster spawning). */
    private static int STAGE_COUNTER = 0;

    /** List of all heroes available for selection at the start of a game. */
    private static List<Heroes> AllHero;

    /** Selected hero to be upgraded, or null if no hero is selected. */
    private static Heroes upgradeHero = null;

    /** Current high-level game state. */
    private static GameState gameState;

    /** Number of rerolls already used. */
    private static int COUNT_REROLL = 0;

    /** Monster factory used to generate monsters for each stage. */
    private static MonsterFactory MONSTER_FACTORY;

    /**
     * Constructs the game engine and starts a new game.
     *
     * <p>This is a convenience constructor that calls {@link #newGame()}.
     */
    public GameEngine() {
        newGame();
    }

    /**
     * Resets the entire game state to start a brand new game.
     *
     * <p>This method:
     * <ul>
     *   <li>Clears hero and monster teams</li>
     *   <li>Resets stage counter and reroll usage</li>
     *   <li>Clears selected upgrade hero</li>
     *   <li>Creates a new {@link MonsterFactory}</li>
     *   <li>Initializes the list of available heroes</li>
     *   <li>Sets {@link GameState} to {@link GameState#START_GAME}</li>
     * </ul>
     */
    public void newGame() {
        if (TEAM == null) TEAM = new ArrayList<>();
        else TEAM.clear();

        if (MONSTER_TEAM == null) MONSTER_TEAM = new ArrayList<>();
        else MONSTER_TEAM.clear();

        setStageCounter(0);
        setCountReroll(0);
        setUpgradeHero(null);

        MONSTER_FACTORY = new MonsterFactory();

        AllHero = List.of(
                new Caster(),
                new Archer(),
                new Tank(),
                new Fighter()
        );

        TEAM = new ArrayList<>();
        gameState = GameState.START_GAME;
    }

    /**
     * Returns the list of heroes that can be selected by the player.
     *
     * @return list of available heroes
     */
    public static List<Heroes> getAvailableHeroes() {
        return AllHero;
    }

    /**
     * Checks whether a hero is already included in the player's team.
     *
     * @param hero hero to check
     * @return true if the hero is already in the team, false otherwise
     */
    public static boolean isInTeam(Heroes hero) {
        return TEAM.contains(hero);
    }

    /**
     * Checks whether the player's team is at maximum capacity.
     *
     * @return true if team size equals {@link #MAX_TEAM_SIZE}, false otherwise
     */
    public static boolean checkFullTeam() {
        return TEAM.size() == MAX_TEAM_SIZE;
    }

    /**
     * Returns the current player hero team.
     *
     * @return hero team
     */
    public static ArrayList<Heroes> getHeroTEAM() {
        return TEAM;
    }

    /**
     * Returns the current team size.
     *
     * @return number of heroes in the team
     */
    public static int getTeamSize() {
        return TEAM.size();
    }

    /**
     * Adds or removes a hero from the player's team.
     *
     * <p>If the hero is already in the team, the hero will be removed.
     * If the hero is not in the team, the hero will be added only if
     * the team is not full.
     *
     * @param hero hero to add or remove
     * @return true if the team was changed, false if the team is full and hero could not be added
     */
    public static boolean toggleTeamMember(Heroes hero) {
        if (isInTeam(hero)) {
            TEAM.remove(hero);
            return true;
        } else if (getTeamSize() >= MAX_TEAM_SIZE) {
            return false;
        } else {
            TEAM.add(hero);
            return true;
        }
    }

    /**
     * Sets the hero that will be upgraded.
     *
     * @param hero hero selected for upgrade, or null to clear selection
     */
    public static void setUpgradeHero(Heroes hero) {
        upgradeHero = hero;
    }

    /**
     * Returns the hero selected for upgrade.
     *
     * @return selected hero for upgrade, or null if none is selected
     */
    public static Heroes getUpgradeHero() {
        return upgradeHero;
    }

    /**
     * Upgrades the selected hero if one has been chosen.
     *
     * <p>This method calls {@link Heroes#upgrade()} on the selected hero.
     */
    public static void upgradingHero() {
        if (getUpgradeHero() != null) {
            upgradeHero.upgrade();
        }
    }

    /**
     * Returns the maximum number of rerolls allowed.
     *
     * @return maximum rerolls
     */
    public static int getMaxReroll() {
        return MAX_REROLL;
    }

    /**
     * Returns the number of rerolls already used.
     *
     * @return reroll usage count
     */
    public static int getCountReroll() {
        return COUNT_REROLL;
    }

    /**
     * Sets the reroll usage count.
     *
     * @param countReroll new reroll count
     */
    public static void setCountReroll(int countReroll) {
        COUNT_REROLL = countReroll;
    }

    /**
     * Adds to the reroll usage count.
     *
     * @param countReroll amount to add
     */
    public static void addCountReroll(int countReroll) {
        COUNT_REROLL += countReroll;
    }

    /**
     * Returns the current stage counter.
     *
     * @return stage counter
     */
    public static int getStageCounter() {
        return STAGE_COUNTER;
    }

    /**
     * Sets the current stage counter.
     *
     * @param stageCounter new stage counter
     */
    public static void setStageCounter(int stageCounter) {
        STAGE_COUNTER = stageCounter;
    }

    /**
     * Adds to the stage counter.
     *
     * @param stageCounter amount to add
     */
    public static void addStageCounter(int stageCounter) {
        STAGE_COUNTER += stageCounter;
    }

    /**
     * Returns the {@link MonsterFactory} used by the game.
     *
     * @return monster factory
     */
    public static MonsterFactory getMonsterFactory() {
        return MONSTER_FACTORY;
    }

    /**
     * Returns the current monster team.
     *
     * @return monster team
     */
    public static ArrayList<Monster> getMonsterTeam() {
        return MONSTER_TEAM;
    }

    /**
     * Spawns a new monster team for the current stage and stores it in {@link #MONSTER_TEAM}.
     *
     * <p>This method delegates monster creation to {@link MonsterFactory#spawnMonster(int)}.
     */
    public static void setMonsterTeam() {
        MONSTER_TEAM = getMonsterFactory().spawnMonster(getStageCounter());
    }

    /**
     * Clears the monster team list.
     *
     * <p>If the monster team list is null, it will be initialized to an empty list.
     */
    public static void clearMonsterTeam() {
        if (MONSTER_TEAM == null) MONSTER_TEAM = new ArrayList<>();
        else MONSTER_TEAM.clear();
    }

    /**
     * Checks whether all heroes in the player team are dead.
     *
     * @return true if all heroes are dead, false otherwise
     */
    public static boolean isHeroAllDead() {
        for (Heroes h : TEAM) {
            if (!h.isDead()) {
                return false;
            }
        }
        return true;
    }

    /**
     * Checks whether all monsters in the monster team are dead.
     *
     * @return true if all monsters are dead, false otherwise
     */
    public static boolean isMonsterAllDead() {
        for (Monster m : MONSTER_TEAM) {
            if (!m.isDead()) {
                return false;
            }
        }
        return true;
    }

    /**
     * Returns the current game state.
     *
     * @return current game state
     */
    public static GameState getGameState() {
        return gameState;
    }

    /**
     * Sets the current game state.
     *
     * @param gameState new game state
     */
    public static void setGameState(GameState gameState) {
        GameEngine.gameState = gameState;
    }
}