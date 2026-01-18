package ru.multivarka;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Locale;
import java.util.Properties;

public class Config {
    private static final int DEFAULT_GAME_ID = 432;
    private static final String USER_AGENT = "mods-audit/1.0 (contact: example@email)";

    private final Path modsDir;
    private final String curseforgeApiKey;
    private final int gameId;
    private final boolean verbose;
    private final Path jsonOutput;
    private final String language;
    private final boolean pauseOnExit;

    public Config(Path modsDir, String curseforgeApiKey, int gameId, boolean verbose, Path jsonOutput, String language,
                  boolean pauseOnExit) {
        this.modsDir = modsDir;
        this.curseforgeApiKey = curseforgeApiKey;
        this.gameId = gameId;
        this.verbose = verbose;
        this.jsonOutput = jsonOutput;
        this.language = language;
        this.pauseOnExit = pauseOnExit;
    }

    public Path getModsDir() {
        return modsDir;
    }

    public String getCurseforgeApiKey() {
        return curseforgeApiKey;
    }

    public int getGameId() {
        return gameId;
    }

    public boolean isVerbose() {
        return verbose;
    }

    public Path getJsonOutput() {
        return jsonOutput;
    }

    public String getLanguage() {
        return language;
    }

    public boolean isPauseOnExit() {
        return pauseOnExit;
    }

    public String userAgent() {
        return USER_AGENT;
    }

    public static Config fromArgs(String[] args) {
        Path configPath = DefaultPaths.defaultConfigPath();
        ensureConfigExists(configPath);
        Properties props = loadProperties(configPath);
        String lang = props.getProperty("language");
        if (StringUtil.isBlank(lang)) {
            lang = defaultLanguage();
        }
        for (int i = 0; i < args.length - 1; i++) {
            if ("--language".equals(args[i])) {
                lang = args[i + 1];
                break;
            }
        }

        Messages messages = Messages.forLanguage(lang);

        Path modsDir = DefaultPaths.defaultModsDir();
        String apiKey = null;
        int gameId = DEFAULT_GAME_ID;
        boolean verbose = false;
        Path jsonOutput = null;
        String language = lang;
        boolean pauseOnExit = isWindows();

        String modsDirProp = props.getProperty("modsDir");
        if (!StringUtil.isBlank(modsDirProp)) {
            modsDir = Paths.get(modsDirProp.trim());
        }

        String apiKeyProp = props.getProperty("curseforgeApiKey");
        if (!StringUtil.isBlank(apiKeyProp)) {
            apiKey = apiKeyProp.trim();
        }

        String gameIdProp = props.getProperty("gameId");
        if (!StringUtil.isBlank(gameIdProp)) {
            try {
                gameId = Integer.parseInt(gameIdProp.trim());
            } catch (NumberFormatException ignored) {
            }
        }

        String verboseProp = props.getProperty("verbose");
        if (!StringUtil.isBlank(verboseProp)) {
            verbose = parseBoolean(verboseProp.trim());
        }

        String jsonOutputProp = props.getProperty("jsonOutput");
        if (!StringUtil.isBlank(jsonOutputProp)) {
            jsonOutput = Paths.get(jsonOutputProp.trim());
        }

        String languageProp = props.getProperty("language");
        if (!StringUtil.isBlank(languageProp)) {
            language = languageProp.trim();
        }

        String pauseProp = props.getProperty("pauseOnExit");
        if (!StringUtil.isBlank(pauseProp)) {
            pauseOnExit = parseBoolean(pauseProp.trim());
        }

        for (int i = 0; i < args.length; i++) {
            String arg = args[i];
            if ("--modsDir".equals(arg)) {
                if (i + 1 >= args.length) {
                    printUsage(messages, messages.msg("error.missing_value", "--modsDir"));
                    return null;
                }
                modsDir = Paths.get(args[++i]);
            } else if ("--curseforgeApiKey".equals(arg)) {
                if (i + 1 >= args.length) {
                    printUsage(messages, messages.msg("error.missing_value", "--curseforgeApiKey"));
                    return null;
                }
                apiKey = args[++i];
            } else if ("--gameId".equals(arg)) {
                if (i + 1 >= args.length) {
                    printUsage(messages, messages.msg("error.missing_value", "--gameId"));
                    return null;
                }
                try {
                    gameId = Integer.parseInt(args[++i]);
                } catch (NumberFormatException e) {
                    printUsage(messages, messages.msg("error.invalid_number", "--gameId"));
                    return null;
                }
            } else if ("--verbose".equals(arg)) {
                verbose = true;
            } else if ("--language".equals(arg)) {
                if (i + 1 >= args.length) {
                    printUsage(messages, messages.msg("error.missing_value", "--language"));
                    return null;
                }
                language = args[++i];
            } else if ("--jsonOutput".equals(arg)) {
                if (i + 1 >= args.length) {
                    printUsage(messages, messages.msg("error.missing_value", "--jsonOutput"));
                    return null;
                }
                jsonOutput = Paths.get(args[++i]);
            } else if ("--pause".equals(arg)) {
                pauseOnExit = true;
            } else if ("--noPause".equals(arg)) {
                pauseOnExit = false;
            } else if ("--help".equals(arg)) {
                printUsage(messages, null);
                return null;
            } else {
                printUsage(messages, messages.msg("error.unknown_arg", arg));
                return null;
            }
        }

        if (StringUtil.isBlank(apiKey)) {
            String envKey = System.getenv("CURSEFORGE_API_KEY");
            if (!StringUtil.isBlank(envKey)) {
                apiKey = envKey.trim();
            }
        }

        if (modsDir == null) {
            modsDir = DefaultPaths.defaultModsDir();
        }

        return new Config(modsDir, apiKey, gameId, verbose, jsonOutput, language, pauseOnExit);
    }

    private static void printUsage(Messages messages, String error) {
        if (error != null) {
            System.err.println(error);
        }
        System.out.println(messages.msg("usage"));
        System.out.println(messages.msg("usage.pause"));
        System.out.println(messages.msg("usage.nopause"));
    }

    private static Properties loadProperties(Path path) {
        Properties props = new Properties();
        if (path == null || !Files.exists(path)) {
            return props;
        }
        try (BufferedReader reader = Files.newBufferedReader(path, StandardCharsets.UTF_8)) {
            props.load(reader);
        } catch (Exception ignored) {
        }
        return props;
    }

    private static void ensureConfigExists(Path path) {
        if (path == null || Files.exists(path)) {
            return;
        }
        try {
            String language = defaultLanguage();
            StringBuilder content = new StringBuilder();
            content.append("# Default configuration for modschecker\n");
            content.append("# Place this file next to the jar or in the working directory.\n\n");
            content.append("# Path to mods directory\n");
            content.append("modsDir=./mods\n\n");
            content.append("# CurseForge API key (can also be set via CURSEFORGE_API_KEY)\n");
            content.append("curseforgeApiKey=\n\n");
            content.append("# Game ID (Minecraft = 432)\n");
            content.append("gameId=432\n\n");
            content.append("# Verbose output\n");
            content.append("verbose=false\n\n");
            content.append("# JSON report output path (optional)\n");
            content.append("jsonOutput=\n\n");
            content.append("# Language: ru or en\n");
            content.append("language=").append(language).append('\n');
            content.append("\n# Pause console after run (useful on Windows)\n");
            content.append("pauseOnExit=false\n");
            try (BufferedWriter writer = Files.newBufferedWriter(path, StandardCharsets.UTF_8)) {
                writer.write(content.toString());
            }
        } catch (Exception ignored) {
        }
    }

    private static boolean parseBoolean(String value) {
        String normalized = value.toLowerCase(Locale.ROOT);
        return "true".equals(normalized) || "yes".equals(normalized) || "1".equals(normalized);
    }

    private static String defaultLanguage() {
        String lang = Locale.getDefault().getLanguage();
        return StringUtil.isBlank(lang) ? "en" : lang;
    }

    private static boolean isWindows() {
        String os = System.getProperty("os.name");
        return os != null && os.toLowerCase(Locale.ROOT).contains("win");
    }
}
