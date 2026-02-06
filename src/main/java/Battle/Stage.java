package Battle;

import Unit.Unit;

import java.util.List;

public class Stage {
    private int level;
    private double atkScale;
    private double hpScale;
    public Stage(int level){

    }

    public void setLevel(int level) {
        this.level = level;
        setAtkScale(0.15);
        setHpScale(0.10);
    }

    public void applyScaling(List<? extends Unit> units){
        for(Unit u :units){
            u.setMaxHp((int)(u.getMaxHp()*hpScale));
            u.setHp(u.getMaxHp());
            u.setAtk((int)(u.getAtk()*atkScale));
        }
    }

    public void setAtkScale(double atkScale) {
        this.atkScale = 1+level*atkScale;
    }

    public void setHpScale(double hpScale) {
        this.hpScale = 1+level*hpScale;
    }
}
