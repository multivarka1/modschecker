package ru.multivarka;

public class ReportSummary {
    private final int total;
    private final int modrinthFound;
    private final int curseforgeFound;
    private final int clientRequired;
    private final int clientPreferred;
    private final int unknown;

    public ReportSummary(int total, int modrinthFound, int curseforgeFound, int clientRequired,
                         int clientPreferred, int unknown) {
        this.total = total;
        this.modrinthFound = modrinthFound;
        this.curseforgeFound = curseforgeFound;
        this.clientRequired = clientRequired;
        this.clientPreferred = clientPreferred;
        this.unknown = unknown;
    }

    public int getTotal() {
        return total;
    }

    public int getModrinthFound() {
        return modrinthFound;
    }

    public int getCurseforgeFound() {
        return curseforgeFound;
    }

    public int getClientRequired() {
        return clientRequired;
    }

    public int getClientPreferred() {
        return clientPreferred;
    }

    public int getUnknown() {
        return unknown;
    }
}
