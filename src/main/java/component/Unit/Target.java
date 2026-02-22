package component.Unit;

import java.util.List;

public final class Target {
    private final Unit single;
    private final List<? extends Unit> group;

    private Target(Unit single, List<? extends Unit> group) {
        this.single = single;
        this.group = group;
    }

    public static Target one(Unit u) {
        return new Target(u, null);
    }

    public static Target many(List<? extends Unit> units) {
        return new Target(null, units);
    }

    public Unit single() { return single; }
    public List<? extends Unit> many() { return group; }

    public boolean isSingle() { return single != null; }
    public boolean isMany() { return group != null; }
}
