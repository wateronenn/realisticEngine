package component.Unit;

import java.util.List;

public final class Target {
    private final Unit single;
    private final List<Unit> group;

    private Target(Unit single, List<Unit> group) {
        this.single = single;
        this.group = group;
    }

    public static Target one(Unit u) {
        return new Target(u, null);
    }

    public static Target many(List<Unit> units) {
        return new Target(null, units);
    }

    public Unit single() { return single; }
    public List<Unit> many() { return group; }

    public boolean isSingle() { return single != null; }
    public boolean isMany() { return group != null; }
}
