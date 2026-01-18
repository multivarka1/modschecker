package ru.multivarka;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class ModsScanner {
    private final ObjectMapper mapper;

    public ModsScanner(ObjectMapper mapper) {
        this.mapper = mapper;
    }

    public List<ModAnalysis> scan(Path modsDir) throws Exception {
        if (!Files.exists(modsDir) || !Files.isDirectory(modsDir)) {
            throw new IllegalArgumentException("modsDir does not exist or is not a directory: " + modsDir);
        }

        List<ModAnalysis> mods = new ArrayList<>();
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(modsDir, path -> {
            String name = path.getFileName().toString().toLowerCase(Locale.ROOT);
            return Files.isRegularFile(path) && name.endsWith(".jar");
        })) {
            for (Path jar : stream) {
                ModAnalysis analysis = new ModAnalysis(jar, jar.getFileName().toString(), Files.size(jar));
                try {
                    analysis.setSha1(HashUtil.sha1Hex(jar));
                    analysis.setFingerprint(CurseForgeFingerprintUtil.fingerprint(jar));
                    analysis.setMetadata(readMetadata(jar));
                } catch (Exception e) {
                    analysis.setScanError(e.getMessage());
                }
                mods.add(analysis);
            }
        }
        return mods;
    }

    private ModMetadata readMetadata(Path jar) {
        try (ZipFile zipFile = new ZipFile(jar.toFile())) {
            ModMetadata fabric = readFabric(zipFile);
            if (fabric != null) {
                return fabric;
            }
            ModMetadata forge = readForge(zipFile);
            if (forge != null) {
                return forge;
            }
            ModMetadata legacy = readLegacy(zipFile);
            if (legacy != null) {
                return legacy;
            }
        } catch (Exception ignored) {
        }
        return null;
    }

    private ModMetadata readFabric(ZipFile zipFile) {
        ZipEntry entry = zipFile.getEntry("fabric.mod.json");
        if (entry == null) {
            return null;
        }
        try (InputStream input = zipFile.getInputStream(entry)) {
            JsonNode root = mapper.readTree(input);
            String id = textValue(root, "id");
            String name = textValue(root, "name");
            String version = textValue(root, "version");
            if (id == null && name == null && version == null) {
                return null;
            }
            return new ModMetadata(id, name, version, MetadataSource.FABRIC);
        } catch (Exception ignored) {
            return null;
        }
    }

    private ModMetadata readForge(ZipFile zipFile) {
        ZipEntry entry = zipFile.getEntry("META-INF/mods.toml");
        if (entry == null) {
            return null;
        }
        String modId = null;
        String displayName = null;
        String version = null;
        boolean inModsBlock = false;

        Pattern pattern = Pattern.compile("^\\s*(modId|displayName|version)\\s*=\\s*\"([^\"]*)\"\\s*$");
        Pattern patternAlt = Pattern.compile("^\\s*(modId|displayName|version)\\s*=\\s*'([^']*)'\\s*$");

        try (InputStream input = zipFile.getInputStream(entry);
             BufferedReader reader = new BufferedReader(new InputStreamReader(input, StandardCharsets.UTF_8))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String trimmed = line.trim();
                if (trimmed.startsWith("[[")) {
                    inModsBlock = "[[mods]]".equalsIgnoreCase(trimmed);
                    if (!inModsBlock && modId != null) {
                        break;
                    }
                    continue;
                }

                if (!inModsBlock) {
                    continue;
                }

                Matcher matcher = pattern.matcher(trimmed);
                Matcher matcherAlt = patternAlt.matcher(trimmed);
                if (matcher.matches()) {
                    String key = matcher.group(1);
                    String value = matcher.group(2);
                    if ("modId".equals(key)) {
                        modId = value;
                    } else if ("displayName".equals(key)) {
                        displayName = value;
                    } else if ("version".equals(key)) {
                        version = value;
                    }
                } else if (matcherAlt.matches()) {
                    String key = matcherAlt.group(1);
                    String value = matcherAlt.group(2);
                    if ("modId".equals(key)) {
                        modId = value;
                    } else if ("displayName".equals(key)) {
                        displayName = value;
                    } else if ("version".equals(key)) {
                        version = value;
                    }
                }

                if (modId != null && displayName != null && version != null) {
                    break;
                }
            }
        } catch (Exception ignored) {
            return null;
        }

        if (modId == null && displayName == null && version == null) {
            return null;
        }
        return new ModMetadata(modId, displayName, version, MetadataSource.FORGE);
    }

    private ModMetadata readLegacy(ZipFile zipFile) {
        ZipEntry entry = zipFile.getEntry("mcmod.info");
        if (entry == null) {
            return null;
        }
        try (InputStream input = zipFile.getInputStream(entry)) {
            JsonNode root = mapper.readTree(input);
            JsonNode node = root;
            if (root.isArray() && root.size() > 0) {
                node = root.get(0);
            } else if (root.has("modList") && root.get("modList").isArray() && root.get("modList").size() > 0) {
                node = root.get("modList").get(0);
            }
            String id = textValue(node, "modid");
            if (id == null) {
                id = textValue(node, "modId");
            }
            String name = textValue(node, "name");
            String version = textValue(node, "version");
            if (id == null && name == null && version == null) {
                return null;
            }
            return new ModMetadata(id, name, version, MetadataSource.LEGACY);
        } catch (Exception ignored) {
            return null;
        }
    }

    private String textValue(JsonNode node, String field) {
        if (node == null || !node.has(field)) {
            return null;
        }
        JsonNode value = node.get(field);
        if (value.isTextual()) {
            return value.asText();
        }
        return null;
    }
}
