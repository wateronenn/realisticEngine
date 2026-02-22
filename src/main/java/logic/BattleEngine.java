package logic;

import component.Unit.Monster;
import component.Unit.Target;
import component.Unit.Unit;
import component.Unit.heroes.Heroes;

import java.util.List;
import java.util.Objects;

import static java.util.Collections.shuffle;

public class BattleEngine {
    private BattleModel model = null;
    private BattleStage stage;
    private BattleListener listener;
    private int turnCounter = 0;

    public BattleEngine(BattleModel model){
        this.model = model;
        setBattleStage(BattleStage.HERO_CHOOSE_SKILL);
        GameEngine.setGameState(GameState.BATTLE);
        model.setActiveHeroIndex(findNextAliveHeroIndex(-1));
        if(model.getActiveHeroIndex()==-1){
            setBattleStage(BattleStage.LOSE_TURN);
            GameEngine.setGameState(GameState.DEFEAT);
        }
    }
    public void setListener(BattleListener listener){
        this.listener = listener;
    }

    public BattleModel getModel() {
        return model;
    }

    public BattleStage getStage() {
        return stage;
    }

    public void beginBattle(){
        emitState(stage);
        emitUpdate();
    }
    //UI use this for choosing skill (activate)
    public boolean onClickHeroSkill(SkillType skill){
        if(stage != BattleStage.HERO_CHOOSE_SKILL) return false;

        Heroes h = getActiveHero();
        if(h==null) return false;
        if(skill == SkillType.SKILL){
            if(!h.canUseSkill()){
                emitLog("Skill is on cooldown");
                return false;
            }
        }
        else if(skill == SkillType.ULTIMATE){
            if(!h.canUseUlt()){
                emitLog("Ultimate is on cooldown");
                return false;
            }
        }
        model.setPendingSkill(skill);
        TargetType t = checkSkillTarget(h,skill);
        if(t == TargetType.ENEMY_ONE){
            setBattleStage(BattleStage.HERO_CHOOSE_TARGET);
        }
        else if(t== TargetType.ALLY_ONE){
            setBattleStage(BattleStage.HERO_CHOOSE_ALLY);
        }
        else{
            setBattleStage(BattleStage.HERO_RESOLVE_ACTION);
            Target target = null;
            if(t == TargetType.ENEMY_ALL) {
                target = Target.many(model.getMONSTER_TEAM());
            }
            else if (t == TargetType.ALLY_ALL){
                target = Target.many(model.getHERO_TEAM());
            }
            else if(t == TargetType.NONE){
                target = Target.one(h);
            }

            resolveHeroAction(h, target, skill);
        }
        emitState(stage);
        emitUpdate();
        return true;

    }

    // Use after Click select Monster
    public boolean onClickChoosingTarget(int monsterIndex){
        if(getBattleStage() != BattleStage.HERO_CHOOSE_TARGET) return false;
        Monster m = getActiveMonster(monsterIndex);
        Target target = Target.one(m);
        Heroes h = getActiveHero();
        SkillType skill = model.getPendingSkill();
        if(m.isDead()) return false;
        setBattleStage(BattleStage.HERO_RESOLVE_ACTION);
        resolveHeroAction(h,target,skill);
        afterHeroAction();

        return true;
    }

    public boolean onClickChoosingAlly(Heroes ally){
        if(getBattleStage() != BattleStage.HERO_CHOOSE_ALLY) return false;
        Target target = Target.one(ally);
        Heroes h = getActiveHero();
        SkillType skill = model.getPendingSkill();

        if(ally.isDead()) return false;
        stage = BattleStage.HERO_RESOLVE_ACTION;
        resolveHeroAction(h,target,skill);
        afterHeroAction();
        return true;
    }

    private int findNextAliveHeroIndex(int currentIdx) {
        List<Heroes> hs = model.getHERO_TEAM();
        if (hs.isEmpty()) return -1;

        for (int step = 1; step <= hs.size(); step++) {
            int idx = (currentIdx + step) % hs.size();
            if (!hs.get(idx).isDead()) return idx;
        }
        return -1;
    }

    private void resolveHeroAction(Heroes h, Target target, SkillType skill){
        h.castSkill(skill,target);
        emitUpdate();
    }

    private void afterHeroAction(){
        model.setPendingSkill(null);
        if(isMonsterAllDead()){
            setBattleStage(BattleStage.WIN_TURN);
            GameEngine.setGameState(GameState.VICTORY);
            emitState(stage);
            emitUpdate();
            return;
        }

        setBattleStage(BattleStage.MONSTER_TURN);
        emitState(stage);
        emitUpdate();
        monsterTurn();

        if(isHeroAllDead()){
            setBattleStage(BattleStage.LOSE_TURN);
            GameEngine.setGameState(GameState.DEFEAT);
            emitState(stage);
            emitUpdate();
            return;
        }
        tickCooldownsForHeroes();
        model.setActiveHeroIndex(findNextAliveHeroIndex(model.getActiveHeroIndex()));

        setBattleStage(BattleStage.HERO_CHOOSE_SKILL);
        emitState(stage);
        emitUpdate();
    }

    private void monsterTurn(){
        for(Monster m : model.getMONSTER_TEAM()){
            if(m.isDead()) continue;
            Heroes target = pickRandomAliveHero();
            if(target == null) return;
            m.attack(target);
            emitUpdate();
            if(isHeroAllDead()) return;
        }
        setBattleStage(BattleStage.HERO_CHOOSE_SKILL);
        emitState(stage);
        emitUpdate();
    }

    private Heroes pickRandomAliveHero(){
        var alive = model.getHERO_TEAM().stream().filter(x -> !x.isDead()).toList();
        if (alive.isEmpty()) return null;
        return alive.get((int)(Math.random() * alive.size()));
    }

    private Heroes getActiveHero(){
        int idx = model.getActiveHeroIndex();
        if (idx < 0 || idx >= model.getHERO_TEAM().size()) return null;
        Heroes h = model.getHERO_TEAM().get(idx);
        return h.isDead() ? null : h;
    }

    private Monster getActiveMonster(int idx){
        if (idx < 0 || idx >= model.getMONSTER_TEAM().size()) return null;
        Monster m = model.getMONSTER_TEAM().get(idx);
        return m.isDead() ? null : m;
    }

    private void tickCooldownsForHeroes(){
        for (Heroes h : model.getHERO_TEAM()) {
            if (h.isDead()) continue;
            h.tickCooldowns(); // best if you implement this in Heroes
            // or do: h.setSkillCd(max(0, h.getSkillCd()-1)) etc.
        }
    }



    private boolean isMonsterAllDead(){
        for(Monster m : model.getMONSTER_TEAM()){
            if(!m.isDead()){
                return  false;
            }
        }
        return  true;
    }
    private boolean isHeroAllDead(){
        for(Heroes h : model.getHERO_TEAM()){
            if(!h.isDead()){
                return  false;
            }
        }
        return  true;
    }
    private static TargetType checkSkillTarget(Heroes h , SkillType skill){
        if(skill == SkillType.NORMAL_ATTACK) return TargetType.ENEMY_ONE;
        if(Objects.equals(h.getHeroClass(), "Tank")){
            if (skill == SkillType.SKILL) return TargetType.ALLY_ONE;
            else return TargetType.ALLY_ALL;
        }
        else if(Objects.equals(h.getHeroClass(), "Caster")){
            if (skill == SkillType.SKILL) return TargetType.ENEMY_ONE;
            else return TargetType.ENEMY_ALL;
        }
        else if(Objects.equals(h.getHeroClass(), "Fighter")){
            return TargetType.ENEMY_ONE;
        }
        else if(Objects.equals(h.getHeroClass(), "Archer")){
            if (skill == SkillType.SKILL) return TargetType.NONE;
            else return TargetType.ENEMY_ALL;
        }
        else return TargetType.ENEMY_ONE;
    }



    public BattleStage getBattleStage() {
        return stage;
    }

    public void setBattleStage(BattleStage battleStage) {
        stage = battleStage;
    }

    public int getTurnCounter() {
        return turnCounter;
    }

    public void setTurnCounter(int turnCounter) {
        this.turnCounter = turnCounter;
    }

    public void emitState(BattleStage s) {
        if (listener != null) listener.onStateChanged(s);
    }

    public void emitUpdate() {
        if (listener != null) listener.onModelUpdated();
    }
    public void emitLog(String msg) {
        if (listener != null) listener.onLog(msg);
    }
}
