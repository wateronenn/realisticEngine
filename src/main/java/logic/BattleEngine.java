package logic;

import component.Monster;
import component.Target;
import component.heroes.Heroes;

import java.util.List;
import java.util.Objects;

import static java.util.Collections.shuffle;

/**
 * Core turn-based battle controller for the game.
 *
 * <p>This engine is responsible for:
 * <ul>
 *   <li>Tracking the current {@link BattleStage} and advancing the battle flow</li>
 *   <li>Handling hero input (choose skill, choose target, choose ally)</li>
 *   <li>Resolving hero actions and coordinating the monster turn</li>
 *   <li>Emitting state updates and logs to the UI through {@link BattleListener}</li>
 * </ul>
 *
 * <p>Typical flow:
 * <ol>
 *   <li>Start at {@link BattleStage#HERO_CHOOSE_SKILL}</li>
 *   <li>Hero selects a skill, possibly selects a target or ally</li>
 *   <li>Engine resolves action, then moves to next hero or to {@link BattleStage#MONSTER_TURN}</li>
 *   <li>UI animates monster attacks using {@link #monsterAttackOne(Monster)} for each monster</li>
 *   <li>UI calls {@link #executeMonsterTurnAndContinue()} after all monster attacks are finished</li>
 * </ol>
 */
public class BattleEngine {
    /** Battle data model containing teams, indexes, and pending action state. */
    private BattleModel model = null;

    /** Current stage of the battle state machine. */
    private BattleStage stage;

    /** Listener used to notify the UI about state changes, model updates, and logs. */
    private BattleListener listener;

    /** Counts completed rounds. Increased after monsters finish their turn. */
    private int turnCounter = 1;

    /**
     * Creates a new battle engine and initializes the battle state.
     *
     * <p>This constructor:
     * <ul>
     *   <li>Sets initial stage to {@link BattleStage#HERO_CHOOSE_SKILL}</li>
     *   <li>Sets global game state to {@link GameState#BATTLE}</li>
     *   <li>Selects the first alive hero as the active hero</li>
     *   <li>If no heroes are alive, sets {@link BattleStage#LOSE_TURN} and {@link GameState#DEFEAT}</li>
     *   <li>Resets per-hero turn state by calling {@code resetAfterTurn()} on each hero</li>
     *   <li>Resets turn counter to 1</li>
     * </ul>
     *
     * @param model battle model containing hero and monster teams and shared state
     */
    public BattleEngine(BattleModel model) {
        this.model = model;
        setBattleStage(BattleStage.HERO_CHOOSE_SKILL);
        GameEngine.setGameState(GameState.BATTLE);

        model.setActiveHeroIndex(findNextAliveHeroIndex(-1));
        if (model.getActiveHeroIndex() == -1) {
            setBattleStage(BattleStage.LOSE_TURN);
            GameEngine.setGameState(GameState.DEFEAT);
        }

        for (Heroes h : model.getHERO_TEAM()) {
            h.resetAfterTurn();
        }
        setTurnCounter(1);
    }

    /**
     * Sets the listener used by the UI layer to receive engine events.
     *
     * @param listener listener that will receive stage changes, model updates, and log messages
     */
    public void setListener(BattleListener listener) {
        this.listener = listener;
    }

    /**
     * Returns the battle model used by this engine.
     *
     * @return battle model
     */
    public BattleModel getModel() {
        return model;
    }

    /**
     * Returns the current battle stage.
     *
     * @return current stage
     */
    public BattleStage getStage() {
        return stage;
    }

    /**
     * Begins the battle by emitting the current stage and a model update to the listener.
     *
     * <p>UI typically calls this once after creating the engine.
     */
    public void beginBattle() {
        emitState(stage);
        emitUpdate();
    }

    /**
     * Handles the UI action where the active hero chooses a skill type.
     *
     * <p>Behavior depends on the current {@link BattleStage} and the selected skill:
     * <ul>
     *   <li>Only works during {@link BattleStage#HERO_CHOOSE_SKILL}</li>
     *   <li>Checks cooldown for {@link SkillType#SKILL} and {@link SkillType#ULTIMATE}</li>
     *   <li>Determines target type using {@link #checkSkillTarget(Heroes, SkillType)}</li>
     *   <li>Moves to target selection stage, ally selection stage, or resolves immediately</li>
     * </ul>
     *
     * @param skill selected skill type
     * @return true if the click was accepted and the engine advanced, false otherwise
     */
    public boolean onClickHeroSkill(SkillType skill) {
        if (stage != BattleStage.HERO_CHOOSE_SKILL) return false;

        System.out.println("Hero choosing skill");
        Heroes h = getActiveHero();
        if (h == null) return false;

        System.out.println("Hero's turn : " + h.getHeroClass());

        if (skill == SkillType.SKILL) {
            if (!h.canUseSkill()) {
                emitLog("Skill is on cooldown");
                return false;
            }
        } else if (skill == SkillType.ULTIMATE) {
            if (!h.canUseUlt()) {
                emitLog("Ultimate is on cooldown");
                return false;
            }
        }

        model.setPendingSkill(skill);
        TargetType t = checkSkillTarget(h, skill);

        if (t == TargetType.ENEMY_ONE) {
            setBattleStage(BattleStage.HERO_CHOOSE_TARGET);
        } else if (t == TargetType.ALLY_ONE) {
            setBattleStage(BattleStage.HERO_CHOOSE_ALLY);
        } else {
            Target target;
            if (t == TargetType.ENEMY_ALL) {
                target = Target.many(model.getMONSTER_TEAM());
            } else if (t == TargetType.ALLY_ALL) {
                target = Target.many(model.getHERO_TEAM());
            } else {
                target = Target.one(h);
            }

            setBattleStage(BattleStage.HERO_RESOLVE_ACTION);
            resolveHeroAction(h, target, skill);
            afterHeroAction();
            //emitState(stage);
            //emitUpdate();
            return true;
        }

        emitState(stage);
        emitUpdate();
        return true;

    }

    /**
     * Handles the UI action where the player selects a monster target by index.
     *
     * <p>This method only works during {@link BattleStage#HERO_CHOOSE_TARGET}.
     * The engine resolves the active hero action against the selected monster.
     *
     * @param monsterIndex index of the monster to target (0-based)
     * @return true if the click was accepted and action resolved, false otherwise
     */
    public boolean onClickChoosingTarget(int monsterIndex) {
        if (getBattleStage() != BattleStage.HERO_CHOOSE_TARGET) return false;

        System.out.println("Hero choosing Target");

        Monster m = getActiveMonster(monsterIndex);
        System.out.println("Attack Monster at index " + monsterIndex);

        Target target = Target.one(m);
        Heroes h = getActiveHero();
        SkillType skill = model.getPendingSkill();

        if (m.isDead()) return false;

        setBattleStage(BattleStage.HERO_RESOLVE_ACTION);
        resolveHeroAction(h, target, skill);
        afterHeroAction();

        return true;
    }

    /**
     * Handles the UI action where the player selects an ally hero target.
     *
     * <p>This method only works during {@link BattleStage#HERO_CHOOSE_ALLY}.
     * The engine resolves the active hero action against the selected ally.
     *
     * @param ally ally hero to target
     * @return true if the click was accepted and action resolved, false otherwise
     */
    public boolean onClickChoosingAlly(Heroes ally) {
        if (getBattleStage() != BattleStage.HERO_CHOOSE_ALLY) return false;

        System.out.println("Hero choosing Ally");

        Target target = Target.one(ally);
        Heroes h = getActiveHero();
        SkillType skill = model.getPendingSkill();

        if (ally.isDead()) return false;

        stage = BattleStage.HERO_RESOLVE_ACTION;
        resolveHeroAction(h, target, skill);
        afterHeroAction();
        return true;
    }

    /**
     * Cancels target or ally selection and returns to {@link BattleStage#HERO_CHOOSE_SKILL}.
     *
     * <p>This only works during {@link BattleStage#HERO_CHOOSE_TARGET} or {@link BattleStage#HERO_CHOOSE_ALLY}.
     * Pending skill selection will be cleared.
     */
    public void onCancelSelection() {
        BattleStage s = getBattleStage();

        // Only allow cancel during hero selection stages
        if (s != BattleStage.HERO_CHOOSE_TARGET && s != BattleStage.HERO_CHOOSE_ALLY) return;

        // Clear whatever pending choice you stored
        model.setPendingSkill(null);

        setBattleStage(BattleStage.HERO_CHOOSE_SKILL);
        emitState(stage);
        emitUpdate();
    }

    /**
     * Finds the next alive hero index after the given index in round-robin order.
     *
     * @param currentIdx current active hero index, or -1 to start from the beginning
     * @return index of next alive hero, or -1 if no alive heroes exist
     */
    private int findNextAliveHeroIndex(int currentIdx) {
        List<Heroes> hs = model.getHERO_TEAM();
        if (hs.isEmpty()) return -1;

        for (int step = 1; step <= hs.size(); step++) {
            int idx = (currentIdx + step) % hs.size();
            if (!hs.get(idx).isDead()) return idx;
        }
        return -1;
    }

    /**
     * Resolves a hero action by delegating to {@link Heroes#castSkill(SkillType, Target)}.
     *
     * <p>This method emits a model update after executing the skill.
     *
     * @param h active hero
     * @param target resolved target container
     * @param skill selected skill type
     */
    private void resolveHeroAction(Heroes h, Target target, SkillType skill) {
        h.castSkill(skill, target);
        System.out.println("[" + getBattleStage() + "] "
                + h.getName() + " Cast " + skill + " target = " + target);
        emitUpdate();
    }

    /**
     * Executes post-hero action logic:
     * <ul>
     *   <li>Clears pending skill</li>
     *   <li>Checks victory condition</li>
     *   <li>Advances to next hero if available</li>
     *   <li>Otherwise moves to {@link BattleStage#MONSTER_TURN}</li>
     * </ul>
     *
     * <p>When the battle ends, this method sets the game state to victory or defeat.
     */
    private void afterHeroAction() {
        model.setPendingSkill(null);

        // 1) victory check
        if (isMonsterAllDead()) {
            setBattleStage(BattleStage.WIN_TURN);
            GameEngine.setGameState(GameState.VICTORY);
            emitState(stage);
            emitUpdate();
            return;
        }

        // 2) next hero OR monster turn
        int nextHeroIdx = findNextAliveHeroIndex(model.getActiveHeroIndex());
        boolean noMoreHeroesThisRound =
                (nextHeroIdx == -1) || (nextHeroIdx <= model.getActiveHeroIndex());

        if (!noMoreHeroesThisRound) {
            model.setActiveHeroIndex(nextHeroIdx);
            setBattleStage(BattleStage.HERO_CHOOSE_SKILL);
            emitState(stage);
            emitUpdate();
            return;
        }

        // 3) round finished -> go to MONSTER_TURN and STOP
        setBattleStage(BattleStage.MONSTER_TURN);
        emitState(stage);
        emitUpdate();
        return;  // ✅ THIS is the missing piece
    }

    /**
     * Performs exactly one monster attack so the UI can animate attacks one by one.
     *
     * <p>The engine selects a random alive hero as the target.
     * If the monster is null or dead, nothing happens.
     *
     * @param m monster that will attack
     */
    public void monsterAttackOne(Monster m) {
        if (m == null || m.isDead()) return;

        Heroes target = pickRandomAliveHero();
        if (target == null) return;

        m.attack(target);
        emitUpdate();
    }

    /**
     * Called by the UI after all monster attacks have completed.
     *
     * <p>This method:
     * <ul>
     *   <li>Checks defeat condition</li>
     *   <li>Ticks cooldowns for all alive heroes</li>
     *   <li>Resets hero shields</li>
     *   <li>Selects the first alive hero for the new round</li>
     *   <li>Increments the turn counter</li>
     *   <li>Moves back to {@link BattleStage#HERO_CHOOSE_SKILL}</li>
     * </ul>
     */
    public void executeMonsterTurnAndContinue() {
        if (isHeroAllDead()) {
            setBattleStage(BattleStage.LOSE_TURN);
            emitState(stage);
            emitUpdate();
            return;
        }

        tickCooldownsForHeroes();
        resetHeroShield();

        model.setActiveHeroIndex(findNextAliveHeroIndex(-1));
        int turn = getTurnCounter();
        setTurnCounter(turn + 1);

        setBattleStage(BattleStage.HERO_CHOOSE_SKILL);
        emitState(stage);
        emitUpdate();
    }

    /**
     * Resets all hero shields to 0.
     */
    private void resetHeroShield() {
        for (Heroes h : model.getHERO_TEAM()) {
            h.setShield(0);
        }
    }

    /**
     * Picks a random alive hero from the hero team.
     *
     * @return random alive hero, or null if all heroes are dead
     */
    private Heroes pickRandomAliveHero() {
        var alive = model.getHERO_TEAM().stream().filter(x -> !x.isDead()).toList();
        if (alive.isEmpty()) return null;
        return alive.get((int) (Math.random() * alive.size()));
    }

    /**
     * Returns the currently active hero, or null if the active index is invalid or the hero is dead.
     *
     * @return active hero or null
     */
    private Heroes getActiveHero() {
        int idx = model.getActiveHeroIndex();
        if (idx < 0 || idx >= model.getHERO_TEAM().size()) return null;
        Heroes h = model.getHERO_TEAM().get(idx);
        return h.isDead() ? null : h;
    }

    /**
     * Returns a monster at a given index, or null if index is invalid or the monster is dead.
     *
     * @param idx monster index
     * @return monster at index or null
     */
    private Monster getActiveMonster(int idx) {
        if (idx < 0 || idx >= model.getMONSTER_TEAM().size()) return null;
        Monster m = model.getMONSTER_TEAM().get(idx);
        return m.isDead() ? null : m;
    }

    /**
     * Decrements cooldown counters for all alive heroes.
     */
    private void tickCooldownsForHeroes() {
        for (Heroes h : model.getHERO_TEAM()) {
            if (h.isDead()) continue;
            h.tickCooldowns();
        }
    }

    /**
     * Checks whether all monsters are dead.
     *
     * @return true if all monsters are dead, false otherwise
     */
    private boolean isMonsterAllDead() {
        for (Monster m : model.getMONSTER_TEAM()) {
            if (!m.isDead()) {
                return false;
            }
        }
        return true;
    }

    /**
     * Checks whether all heroes are dead.
     *
     * @return true if all heroes are dead, false otherwise
     */
    private boolean isHeroAllDead() {
        for (Heroes h : model.getHERO_TEAM()) {
            if (!h.isDead()) {
                return false;
            }
        }
        return true;
    }

    /**
     * Determines the targeting behavior for a hero class and skill type.
     *
     * <p>This method is used to decide whether the UI should request a single enemy,
     * a single ally, all enemies, all allies, or no explicit target selection.
     *
     * @param h hero whose class is used to select target rules
     * @param skill chosen skill type
     * @return target type required by this hero class for the selected skill
     */
    private static TargetType checkSkillTarget(Heroes h, SkillType skill) {
        if (skill == SkillType.NORMAL_ATTACK) return TargetType.ENEMY_ONE;

        if (Objects.equals(h.getHeroClass(), "Tank")) {
            if (skill == SkillType.SKILL) return TargetType.ALLY_ONE;
            else return TargetType.ALLY_ALL;
        } else if (Objects.equals(h.getHeroClass(), "Caster")) {
            if (skill == SkillType.SKILL) return TargetType.ENEMY_ONE;
            else return TargetType.ENEMY_ALL;
        } else if (Objects.equals(h.getHeroClass(), "Fighter")) {
            return TargetType.ENEMY_ONE;
        } else if (Objects.equals(h.getHeroClass(), "Archer")) {
            if (skill == SkillType.SKILL) return TargetType.NONE;
            else return TargetType.ENEMY_ALL;
        } else {
            return TargetType.ENEMY_ONE;
        }
    }

    /**
     * Returns the current battle stage.
     *
     * @return current stage
     */
    public BattleStage getBattleStage() {
        return stage;
    }

    /**
     * Sets the current battle stage without emitting any events.
     *
     * @param battleStage new stage
     */
    public void setBattleStage(BattleStage battleStage) {
        stage = battleStage;
    }

    /**
     * Returns the current turn counter.
     *
     * @return turn counter
     */
    public int getTurnCounter() {
        return turnCounter;
    }

    /**
     * Sets the current turn counter.
     *
     * @param turnCounter new turn counter
     */
    public void setTurnCounter(int turnCounter) {
        this.turnCounter = turnCounter;
    }

    /**
     * Emits a stage change event to the listener.
     *
     * @param s new stage
     */
    public void emitState(BattleStage s) {
        if (listener != null) listener.onStateChanged(s);
    }

    /**
     * Emits a model update event to the listener.
     */
    public void emitUpdate() {
        if (listener != null) listener.onModelUpdated();
    }

    /**
     * Emits a log message event to the listener.
     *
     * @param msg log message to display
     */
    public void emitLog(String msg) {
        if (listener != null) listener.onLog(msg);
    }
}