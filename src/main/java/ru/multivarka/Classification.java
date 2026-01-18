package ru.multivarka;

public class Classification {
    private final ClassificationType type;
    private final String reason;
    private final boolean heuristic;

    public Classification(ClassificationType type, String reason, boolean heuristic) {
        this.type = type;
        this.reason = reason;
        this.heuristic = heuristic;
    }

    public ClassificationType getType() {
        return type;
    }

    public String getReason() {
        return reason;
    }

    public boolean isHeuristic() {
        return heuristic;
    }
}
