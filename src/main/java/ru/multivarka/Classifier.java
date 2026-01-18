package ru.multivarka;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;

public class Classifier {
    private static final List<String> HEURISTIC_KEYWORDS = Arrays.asList(
            "client",
            "hud",
            "minimap",
            "shader",
            "gui",
            "inventory",
            "zoom",
            "overlay"
    );

    public Classification classify(ModAnalysis mod) {
        ModrinthProject project = mod.getModrinthProject();
        if (project != null) {
            String clientSide = normalize(project.getClientSide());
            String serverSide = normalize(project.getServerSide());
            if ("required".equals(clientSide) && "unsupported".equals(serverSide)) {
                return new Classification(ClassificationType.CLIENT_REQUIRED,
                        "classification.modrinth.required_unsupported", false);
            }
            if ("required".equals(clientSide) && "optional".equals(serverSide)) {
                return new Classification(ClassificationType.CLIENT_PREFERRED,
                        "classification.modrinth.required_optional", false);
            }
        }

        String target = heuristicTarget(mod);
        if (target != null) {
            String lowered = target.toLowerCase(Locale.ROOT);
            for (String keyword : HEURISTIC_KEYWORDS) {
                if (lowered.contains(keyword)) {
                    return new Classification(ClassificationType.CLIENT_PREFERRED,
                            "classification.heuristic:" + keyword, true);
                }
            }
        }

        return new Classification(ClassificationType.UNKNOWN, "classification.unknown", false);
    }

    private String heuristicTarget(ModAnalysis mod) {
        ModMetadata metadata = mod.getMetadata();
        if (metadata != null) {
            if (!StringUtil.isBlank(metadata.getName())) {
                return metadata.getName();
            }
            if (!StringUtil.isBlank(metadata.getId())) {
                return metadata.getId();
            }
        }
        return mod.getFileName();
    }

    private String normalize(String value) {
        return value == null ? null : value.toLowerCase(Locale.ROOT);
    }
}
