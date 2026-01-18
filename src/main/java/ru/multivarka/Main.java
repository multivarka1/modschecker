package ru.multivarka;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.nio.file.Path;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        Config config = Config.fromArgs(args);
        if (config == null) {
            return;
        }

        Messages messages = Messages.forLanguage(config.getLanguage());
        ObjectMapper mapper = new ObjectMapper()
                .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
                .setSerializationInclusion(JsonInclude.Include.NON_NULL);

        ModsScanner scanner = new ModsScanner(mapper);
        List<ModAnalysis> mods;
        try {
            mods = scanner.scan(config.getModsDir());
        } catch (Exception e) {
            System.err.println(messages.msg("error.scan", e.getMessage()));
            return;
        }

        ModrinthClient modrinthClient = new ModrinthClient(mapper, config.userAgent());
        for (ModAnalysis mod : mods) {
            if (mod.getSha1() == null) {
                mod.setModrinthReason("modrinth.missing_sha1");
                continue;
            }
            ModrinthClient.ModrinthLookup lookup = modrinthClient.lookupBySha1(mod.getSha1());
            mod.setModrinthProject(lookup.getProject().orElse(null));
            mod.setModrinthReason(lookup.getError());
        }

        if (config.getCurseforgeApiKey() != null) {
            CurseForgeClient curseForgeClient = new CurseForgeClient(mapper, config.getCurseforgeApiKey(), config.userAgent());
            curseForgeClient.enrichMods(mods);
        } else {
            for (ModAnalysis mod : mods) {
                mod.setCurseForgeReason("curseforge.no_api_key");
            }
        }

        Classifier classifier = new Classifier();
        for (ModAnalysis mod : mods) {
            mod.setClassification(classifier.classify(mod));
        }

        ConsoleReporter reporter = new ConsoleReporter(mapper, messages);
        reporter.printReport(mods, config.isVerbose());

        if (config.getJsonOutput() != null) {
            try {
                reporter.writeJsonReport(mods, config.getJsonOutput());
                System.out.println(messages.msg("json.saved", config.getJsonOutput().toAbsolutePath()));
            } catch (Exception e) {
                System.err.println(messages.msg("error.json", e.getMessage()));
            }
        }

        if (config.isPauseOnExit()) {
            waitForExit(messages);
        }
    }

    private static void waitForExit(Messages messages) {
        System.out.println(messages.msg("prompt.pause"));
        try {
            byte[] buffer = new byte[1];
            while (System.in.read(buffer) != -1) {
                if (buffer[0] == '\n') {
                    break;
                }
            }
        } catch (Exception ignored) {
        }
    }
}
