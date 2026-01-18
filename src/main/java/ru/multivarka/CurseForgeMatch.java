package ru.multivarka;

public class CurseForgeMatch {
    private final long modId;
    private final long fileId;
    private final String modSlug;
    private final String modName;
    private final String fileDisplayName;
    private final String fileName;

    public CurseForgeMatch(long modId, long fileId, String modSlug, String modName, String fileDisplayName, String fileName) {
        this.modId = modId;
        this.fileId = fileId;
        this.modSlug = modSlug;
        this.modName = modName;
        this.fileDisplayName = fileDisplayName;
        this.fileName = fileName;
    }

    public long getModId() {
        return modId;
    }

    public long getFileId() {
        return fileId;
    }

    public String getModSlug() {
        return modSlug;
    }

    public String getModName() {
        return modName;
    }

    public String getFileDisplayName() {
        return fileDisplayName;
    }

    public String getFileName() {
        return fileName;
    }

    public String projectUrl() {
        if (!StringUtil.isBlank(modSlug)) {
            return "https://www.curseforge.com/minecraft/mc-mods/" + modSlug;
        }
        if (modId > 0) {
            return "https://www.curseforge.com/minecraft/mc-mods/" + modId;
        }
        return null;
    }
}
