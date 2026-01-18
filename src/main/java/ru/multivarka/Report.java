package ru.multivarka;

import java.util.List;

public class Report {
    private final ReportSummary summary;
    private final List<ReportMod> mods;

    public Report(ReportSummary summary, List<ReportMod> mods) {
        this.summary = summary;
        this.mods = mods;
    }

    public ReportSummary getSummary() {
        return summary;
    }

    public List<ReportMod> getMods() {
        return mods;
    }
}
