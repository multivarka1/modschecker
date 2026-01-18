package ru.multivarka;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class ModrinthClient {
    private static final String BASE_URL = "https://api.modrinth.com";
    private final ObjectMapper mapper;
    private final String userAgent;
    private final Map<String, ModrinthLookup> cache = new HashMap<>();

    public ModrinthClient(ObjectMapper mapper, String userAgent) {
        this.mapper = mapper;
        this.userAgent = userAgent;
    }

    public ModrinthLookup lookupBySha1(String sha1) {
        if (sha1 == null) {
            return new ModrinthLookup(Optional.empty(), "modrinth.missing_sha1");
        }
        ModrinthLookup cached = cache.get(sha1);
        if (cached != null) {
            return cached;
        }

        try {
            String versionUrl = BASE_URL + "/v2/version_file/" + sha1 + "?algorithm=sha1";
            Map<String, String> headers = new HashMap<>();
            headers.put("User-Agent", userAgent);
            HttpUtil.HttpResult versionResponse = HttpUtil.get(versionUrl, headers, 20000);
            int status = versionResponse.getStatusCode();
            if (status == 404) {
                ModrinthLookup result = new ModrinthLookup(Optional.empty(), "modrinth.not_found");
                cache.put(sha1, result);
                return result;
            }
            if (status == 429) {
                ModrinthLookup result = new ModrinthLookup(Optional.empty(), "modrinth.rate_limited");
                cache.put(sha1, result);
                return result;
            }
            if (status < 200 || status >= 300) {
                ModrinthLookup result = new ModrinthLookup(Optional.empty(), "modrinth.http:" + status);
                cache.put(sha1, result);
                return result;
            }

            ModrinthVersionResponse version = mapper.readValue(versionResponse.getBody(), ModrinthVersionResponse.class);
            if (version.getProjectId() == null || StringUtil.isBlank(version.getProjectId())) {
                ModrinthLookup result = new ModrinthLookup(Optional.empty(), "modrinth.error:missing project_id");
                cache.put(sha1, result);
                return result;
            }

            String projectUrl = BASE_URL + "/v2/project/" + version.getProjectId();
            HttpUtil.HttpResult projectResponse = HttpUtil.get(projectUrl, headers, 20000);
            int projectStatus = projectResponse.getStatusCode();
            if (projectStatus < 200 || projectStatus >= 300) {
                ModrinthLookup result = new ModrinthLookup(Optional.empty(), "modrinth.project_http:" + projectStatus);
                cache.put(sha1, result);
                return result;
            }

            ModrinthProjectResponse project = mapper.readValue(projectResponse.getBody(), ModrinthProjectResponse.class);
            ModrinthProject resultProject = new ModrinthProject(
                    project.getId(),
                    project.getSlug(),
                    project.getTitle(),
                    project.getClientSide(),
                    project.getServerSide(),
                    project.getCategories(),
                    project.getDownloads()
            );
            ModrinthLookup result = new ModrinthLookup(Optional.of(resultProject), null);
            cache.put(sha1, result);
            return result;
        } catch (Exception e) {
            String message = e.getMessage();
            if (StringUtil.isBlank(message)) {
                message = e.getClass().getSimpleName();
            }
            ModrinthLookup result = new ModrinthLookup(Optional.empty(), "modrinth.error:" + message);
            cache.put(sha1, result);
            return result;
        }
    }

    public static class ModrinthLookup {
        private final Optional<ModrinthProject> project;
        private final String error;

        public ModrinthLookup(Optional<ModrinthProject> project, String error) {
            this.project = project;
            this.error = error;
        }

        public Optional<ModrinthProject> getProject() {
            return project;
        }

        public String getError() {
            return error;
        }
    }

    private static class ModrinthVersionResponse {
        @JsonProperty("project_id")
        public String projectId;

        public ModrinthVersionResponse() {
        }

        public String getProjectId() {
            return projectId;
        }
    }

    private static class ModrinthProjectResponse {
        public String id;
        public String slug;
        public String title;
        @JsonProperty("client_side")
        public String clientSide;
        @JsonProperty("server_side")
        public String serverSide;
        public List<String> categories;
        public Long downloads;

        public ModrinthProjectResponse() {
        }

        public String getId() {
            return id;
        }

        public String getSlug() {
            return slug;
        }

        public String getTitle() {
            return title;
        }

        public String getClientSide() {
            return clientSide;
        }

        public String getServerSide() {
            return serverSide;
        }

        public List<String> getCategories() {
            return categories;
        }

        public Long getDownloads() {
            return downloads;
        }
    }
}
