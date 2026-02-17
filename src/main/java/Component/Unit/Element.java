package Component.Unit;

public enum Element {WATER,FIRE,NATURE,LIGHT,DARK;
    public double getModifierAgainst(Element target){
        if(this == target ) return 0.8;

        switch (this){
            case WATER: return target == FIRE ? 1.2:1.0;
            case FIRE:return target==NATURE ? 1.2:1.0;
            case NATURE:return target==WATER ? 1.2:1.0;
            case LIGHT:return target==DARK ? 1.2:1.0;
            case DARK: return target==LIGHT ? 1.2:1.0;
            default:return 1.0;
        }
    }
}
