package ru.multivarka;

import java.util.List;

public class ModrinthProject {
    private final String id;
    private final String slug;
    private final String title;
    private final String clientSide;
    private final String serverSide;
    private final List<String> categories;
    private final Long downloads;

    public ModrinthProject(String id, String slug, String title, String clientSide, String serverSide,
                           List<String> categories, Long downloads) {
        this.id = id;
        this.slug = slug;
        this.title = title;
        this.clientSide = clientSide;
        this.serverSide = serverSide;
        this.categories = categories;
        this.downloads = downloads;
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

    public String projectUrl() {
        if (StringUtil.isBlank(slug)) {
            return null;
        }
        return "https://modrinth.com/mod/" + slug;
    }
}
