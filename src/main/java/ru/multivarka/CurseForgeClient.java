package ru.multivarka;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class CurseForgeClient {
    private static final String BASE_URL = "https://api.curseforge.com";
    private final ObjectMapper mapper;
    private final String apiKey;
    private final String userAgent;
    private final Map<Long, CurseForgeMod> modCache = new HashMap<>();

    public CurseForgeClient(ObjectMapper mapper, String apiKey, String userAgent) {
        this.mapper = mapper;
        this.apiKey = apiKey;
        this.userAgent = userAgent;
    }

    public void enrichMods(List<ModAnalysis> mods) {
        List<Long> fingerprints = new ArrayList<>();
        for (ModAnalysis mod : mods) {
            if (mod.getScanError() != null) {
                mod.setCurseForgeReason("curseforge.scan_error:" + mod.getScanError());
                continue;
            }
            fingerprints.add(mod.getFingerprint());
        }

        if (fingerprints.isEmpty()) {
            return;
        }

        Map<Long, ExactMatch> matches;
        try {
            matches = lookupFingerprints(fingerprints);
        } catch (Exception e) {
            for (ModAnalysis mod : mods) {
                String message = e.getMessage();
                if (StringUtil.isBlank(message)) {
                    message = e.getClass().getSimpleName();
                }
                if (message.startsWith("curseforge.")) {
                    mod.setCurseForgeReason(message);
                } else {
                    mod.setCurseForgeReason("curseforge.error:" + message);
                }
            }
            return;
        }

        Set<Long> modIds = new HashSet<>();
        for (ExactMatch match : matches.values()) {
            modIds.add(match.getId());
        }

        for (Long modId : modIds) {
            modCache.computeIfAbsent(modId, this::fetchModInfo);
        }

        for (ModAnalysis mod : mods) {
            ExactMatch match = matches.get(mod.getFingerprint());
            if (match == null) {
                if (mod.getCurseForgeReason() == null) {
                    mod.setCurseForgeReason("curseforge.no_match");
                }
                continue;
            }
            CurseForgeMod modInfo = modCache.get(match.getId());
            String slug = modInfo != null ? modInfo.getSlug() : null;
            String name = modInfo != null ? modInfo.getName() : null;
            long fileId = match.getFile() != null ? match.getFile().getId() : 0;
            String fileName = match.getFile() != null ? match.getFile().getFileName() : null;
            String displayName = match.getFile() != null ? match.getFile().getDisplayName() : null;
            mod.setCurseForgeMatch(new CurseForgeMatch(match.getId(), fileId, slug, name, displayName, fileName));
        }
    }

    private Map<Long, ExactMatch> lookupFingerprints(List<Long> fingerprints) throws Exception {
        ObjectNode body = mapper.createObjectNode();
        body.putPOJO("fingerprints", fingerprints);
        String json = mapper.writeValueAsString(body);

        Map<String, String> headers = new HashMap<>();
        headers.put("x-api-key", apiKey);
        headers.put("User-Agent", userAgent);
        headers.put("Content-Type", "application/json");
        HttpUtil.HttpResult response = HttpUtil.post(BASE_URL + "/v1/fingerprints", json, headers, 20000);
        int status = response.getStatusCode();
        if (status == 429) {
            throw new IllegalStateException("curseforge.rate_limited");
        }
        if (status < 200 || status >= 300) {
            throw new IllegalStateException("curseforge.http:" + status);
        }

        FingerprintsResponse responseBody = mapper.readValue(response.getBody(), FingerprintsResponse.class);
        Map<Long, ExactMatch> result = new HashMap<>();
        if (responseBody != null && responseBody.getData() != null && responseBody.getData().getExactMatches() != null) {
            for (ExactMatch match : responseBody.getData().getExactMatches()) {
                if (match.getFile() != null) {
                    result.put(match.getFile().getFileFingerprint(), match);
                }
            }
        }
        return result;
    }

    private CurseForgeMod fetchModInfo(long modId) {
        try {
            Map<String, String> headers = new HashMap<>();
            headers.put("x-api-key", apiKey);
            headers.put("User-Agent", userAgent);
            HttpUtil.HttpResult response = HttpUtil.get(BASE_URL + "/v1/mods/" + modId, headers, 20000);
            if (response.getStatusCode() < 200 || response.getStatusCode() >= 300) {
                return null;
            }
            ModResponse modResponse = mapper.readValue(response.getBody(), ModResponse.class);
            if (modResponse == null || modResponse.getData() == null) {
                return null;
            }
            return modResponse.getData();
        } catch (Exception ignored) {
            return null;
        }
    }

    private static class FingerprintsResponse {
        public FingerprintsData data;

        public FingerprintsResponse() {
        }

        public FingerprintsData getData() {
            return data;
        }
    }

    private static class FingerprintsData {
        @JsonProperty("exactMatches")
        public List<ExactMatch> exactMatches;

        public FingerprintsData() {
        }

        public List<ExactMatch> getExactMatches() {
            return exactMatches;
        }
    }

    private static class ExactMatch {
        public long id;
        public FileInfo file;

        public ExactMatch() {
        }

        public long getId() {
            return id;
        }

        public FileInfo getFile() {
            return file;
        }
    }

    private static class FileInfo {
        public long id;
        public String fileName;
        public String displayName;
        @JsonProperty("fileFingerprint")
        public long fileFingerprint;

        public FileInfo() {
        }

        public long getId() {
            return id;
        }

        public String getFileName() {
            return fileName;
        }

        public String getDisplayName() {
            return displayName;
        }

        public long getFileFingerprint() {
            return fileFingerprint;
        }
    }

    private static class ModResponse {
        public CurseForgeMod data;

        public ModResponse() {
        }

        public CurseForgeMod getData() {
            return data;
        }
    }

    private static class CurseForgeMod {
        public long id;
        public String name;
        public String slug;

        public CurseForgeMod() {
        }

        public long getId() {
            return id;
        }

        public String getName() {
            return name;
        }

        public String getSlug() {
            return slug;
        }
    }
}
