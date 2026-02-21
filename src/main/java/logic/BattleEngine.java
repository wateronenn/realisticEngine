package logic;

import component.Unit.Monster;
import component.Unit.heroes.Heroes;
import javafx.application.Platform;

import java.util.ArrayList;

public class BattleEngine {
    private static BattleStage battleStage;
    private static int turnCounter;

    public static void beginBattle(){
        GameEngine.setGameState(GameState.BATTLE);
        setBattleStage(BattleStage.START_BATTLE);
        //TODO & complete later
        Thread t = new Thread(()->{
            try{
                while(GameEngine.getGameState()== GameState.BATTLE){
                    Thread.sleep(1000);
                    Platform.runLater(BattleEngine::heroTurn);
                    Thread.sleep(1500);
                    if(GameEngine.getGameState()== GameState.BATTLE)) {
                        Platform.runLater(BattleEngine::monsterTurn);
                    }
                }
            }catch (InterruptedException e){
                Thread.currentThread().interrupt();
                e.printStackTrace();
            }

        });
        t.setDaemon(true);
        t.start();

    }

    public static void heroTurn(){
        setBattleStage(BattleStage.HERO_TURN);
        // show UI for Hero skill
        ArrayList<Heroes> TEAM = GameEngine.getHeroTEAM();
        for(Heroes h : TEAM){
            // 1. choose skill to cast

            // 2. choose monster to attack
        }
    }

    public static void monsterTurn(){
        setBattleStage(BattleStage.MONSTER_TURN);
    }

    public static BattleStage getBattleStage() {
        return battleStage;
    }

    public static void setBattleStage(BattleStage battleStage) {
        BattleEngine.battleStage = battleStage;
    }
}
