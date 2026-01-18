package ru.multivarka;

public class ReportMod {
    private final String fileName;
    private final String path;
    private final long size;
    private final String sha1;
    private final long fingerprint;
    private final ModMetadata metadata;
    private final ModrinthProject modrinth;
    private final CurseForgeMatch curseforge;
    private final Classification classification;
    private final String modrinthReason;
    private final String curseforgeReason;

    public ReportMod(String fileName, String path, long size, String sha1, long fingerprint, ModMetadata metadata,
                     ModrinthProject modrinth, CurseForgeMatch curseforge, Classification classification,
                     String modrinthReason, String curseforgeReason) {
        this.fileName = fileName;
        this.path = path;
        this.size = size;
        this.sha1 = sha1;
        this.fingerprint = fingerprint;
        this.metadata = metadata;
        this.modrinth = modrinth;
        this.curseforge = curseforge;
        this.classification = classification;
        this.modrinthReason = modrinthReason;
        this.curseforgeReason = curseforgeReason;
    }

    public String getFileName() {
        return fileName;
    }

    public String getPath() {
        return path;
    }

    public long getSize() {
        return size;
    }

    public String getSha1() {
        return sha1;
    }

    public long getFingerprint() {
        return fingerprint;
    }

    public ModMetadata getMetadata() {
        return metadata;
    }

    public ModrinthProject getModrinth() {
        return modrinth;
    }

    public CurseForgeMatch getCurseforge() {
        return curseforge;
    }

    public Classification getClassification() {
        return classification;
    }

    public String getModrinthReason() {
        return modrinthReason;
    }

    public String getCurseforgeReason() {
        return curseforgeReason;
    }
}
