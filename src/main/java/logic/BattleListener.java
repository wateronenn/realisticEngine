package logic;

public interface BattleListener {
    void onStateChanged(BattleStage stage);
    void onLog(String message);
    void onModelUpdated();
}
