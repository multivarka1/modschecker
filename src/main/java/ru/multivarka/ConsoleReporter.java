package ru.multivarka;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class ConsoleReporter {
    private final ObjectMapper mapper;
    private final Messages messages;
    private final boolean useColor;

    private static final String COLOR_RED = "\u001b[31m";
    private static final String COLOR_YELLOW = "\u001b[33m";
    private static final String COLOR_RESET = "\u001b[0m";

    public ConsoleReporter(ObjectMapper mapper, Messages messages) {
        this.mapper = mapper;
        this.messages = messages;
        this.useColor = System.console() != null;
    }

    public void printReport(List<ModAnalysis> mods, boolean verbose) {
        int total = mods.size();
        int modrinthFound = 0;
        int curseforgeFound = 0;
        int clientRequired = 0;
        int clientPreferred = 0;
        int unknown = 0;

        for (ModAnalysis mod : mods) {
            if (mod.getModrinthProject() != null) {
                modrinthFound++;
            }
            if (mod.getCurseForgeMatch() != null) {
                curseforgeFound++;
            }
            Classification classification = mod.getClassification();
            if (classification != null) {
                if (classification.getType() == ClassificationType.CLIENT_REQUIRED) {
                    clientRequired++;
                } else if (classification.getType() == ClassificationType.CLIENT_PREFERRED) {
                    clientPreferred++;
                } else {
                    unknown++;
                }
            }
        }

        System.out.println(messages.msg("scan.mode"));
        System.out.println(messages.msg("summary"));
        System.out.println(messages.msg("summary.total", total));
        System.out.println(messages.msg("summary.modrinth", modrinthFound));
        System.out.println(messages.msg("summary.curseforge", curseforgeFound));
        System.out.println(messages.msg("summary.client_required", clientRequired));
        System.out.println(messages.msg("summary.client_preferred", clientPreferred));
        System.out.println(messages.msg("summary.unknown", unknown));

        for (ModAnalysis mod : mods) {
            Classification classification = mod.getClassification();
            if (classification == null || classification.getType() == ClassificationType.UNKNOWN) {
                continue;
            }
            System.out.println();
            System.out.println(messages.msg("client.mod"));
            System.out.println(messages.msg("field.file", mod.getFileName()));
            System.out.println(messages.msg("field.path", mod.getPath().toAbsolutePath()));
            ModMetadata metadata = mod.getMetadata();
            if (metadata != null) {
                System.out.println(messages.msg("field.metadata",
                        metadata.getSource(),
                        safe(metadata.getId()),
                        safe(metadata.getName()),
                        safe(metadata.getVersion())));
            }
            ModrinthProject modrinth = mod.getModrinthProject();
            if (modrinth != null) {
                System.out.println(messages.msg("field.modrinth",
                        safe(modrinth.getTitle()),
                        safe(modrinth.getSlug()),
                        safe(modrinth.getClientSide()),
                        safe(modrinth.getServerSide())));
                if (modrinth.projectUrl() != null) {
                    System.out.println(messages.msg("field.modrinth.url", modrinth.projectUrl()));
                }
            }
            CurseForgeMatch curseForge = mod.getCurseForgeMatch();
            if (curseForge != null) {
                System.out.println(messages.msg("field.curseforge",
                        curseForge.getModId(),
                        curseForge.getFileId(),
                        safe(curseForge.getModName()),
                        safe(curseForge.getFileDisplayName())));
                if (curseForge.projectUrl() != null) {
                    System.out.println(messages.msg("field.curseforge.url", curseForge.projectUrl()));
                }
            }
            String reason = messages.classificationReason(classification.getReason(), classification.isHeuristic());
            reason = colorizeReason(classification.getReason(), reason);
            System.out.println(messages.msg("field.reason", reason));
        }

        if (verbose) {
            System.out.println();
            System.out.println(messages.msg("unmatched.details"));
            for (ModAnalysis mod : mods) {
                List<String> reasons = new ArrayList<>();
                if (mod.getModrinthProject() == null) {
                    reasons.add(messages.msg("label.modrinth", messages.reason(mod.getModrinthReason())));
                }
                if (mod.getCurseForgeMatch() == null) {
                    reasons.add(messages.msg("label.curseforge", messages.reason(mod.getCurseForgeReason())));
                }
                if (reasons.isEmpty() && mod.getScanError() != null) {
                    reasons.add(messages.msg("field.scan", mod.getScanError()));
                }
                if (!reasons.isEmpty()) {
                    System.out.println("  " + mod.getFileName() + " -> " + String.join(", ", reasons));
                }
            }
        }
    }

    public void writeJsonReport(List<ModAnalysis> mods, Path outputPath) throws Exception {
        ReportSummary summary = summarize(mods);
        List<ReportMod> entries = new ArrayList<>();
        for (ModAnalysis mod : mods) {
            entries.add(new ReportMod(
                    mod.getFileName(),
                    mod.getPath().toAbsolutePath().toString(),
                    mod.getSize(),
                    mod.getSha1(),
                    mod.getFingerprint(),
                    mod.getMetadata(),
                    mod.getModrinthProject(),
                    mod.getCurseForgeMatch(),
                    mod.getClassification(),
                    mod.getModrinthReason(),
                    mod.getCurseForgeReason()
            ));
        }
        Report report = new Report(summary, entries);
        mapper.writerWithDefaultPrettyPrinter().writeValue(outputPath.toFile(), report);
    }

    private ReportSummary summarize(List<ModAnalysis> mods) {
        int total = mods.size();
        int modrinthFound = 0;
        int curseforgeFound = 0;
        int clientRequired = 0;
        int clientPreferred = 0;
        int unknown = 0;

        for (ModAnalysis mod : mods) {
            if (mod.getModrinthProject() != null) {
                modrinthFound++;
            }
            if (mod.getCurseForgeMatch() != null) {
                curseforgeFound++;
            }
            Classification classification = mod.getClassification();
            if (classification != null) {
                if (classification.getType() == ClassificationType.CLIENT_REQUIRED) {
                    clientRequired++;
                } else if (classification.getType() == ClassificationType.CLIENT_PREFERRED) {
                    clientPreferred++;
                } else {
                    unknown++;
                }
            }
        }

        return new ReportSummary(total, modrinthFound, curseforgeFound, clientRequired, clientPreferred, unknown);
    }

    private String safe(String value) {
        return value == null ? messages.msg("n/a") : value;
    }

    private String colorizeReason(String code, String text) {
        if (!useColor || code == null) {
            return text;
        }
        if ("classification.modrinth.required_unsupported".equals(code)) {
            return COLOR_RED + text + COLOR_RESET;
        }
        if ("classification.modrinth.required_optional".equals(code)) {
            return COLOR_YELLOW + text + COLOR_RESET;
        }
        return text;
    }
}
