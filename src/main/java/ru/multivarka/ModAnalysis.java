package ru.multivarka;

import java.nio.file.Path;

public class ModAnalysis {
    private final Path path;
    private final String fileName;
    private final long size;
    private String sha1;
    private long fingerprint;
    private ModMetadata metadata;
    private ModrinthProject modrinthProject;
    private CurseForgeMatch curseForgeMatch;
    private Classification classification;
    private String modrinthReason;
    private String curseForgeReason;
    private String scanError;

    public ModAnalysis(Path path, String fileName, long size) {
        this.path = path;
        this.fileName = fileName;
        this.size = size;
    }

    public Path getPath() {
        return path;
    }

    public String getFileName() {
        return fileName;
    }

    public long getSize() {
        return size;
    }

    public String getSha1() {
        return sha1;
    }

    public void setSha1(String sha1) {
        this.sha1 = sha1;
    }

    public long getFingerprint() {
        return fingerprint;
    }

    public void setFingerprint(long fingerprint) {
        this.fingerprint = fingerprint;
    }

    public ModMetadata getMetadata() {
        return metadata;
    }

    public void setMetadata(ModMetadata metadata) {
        this.metadata = metadata;
    }

    public ModrinthProject getModrinthProject() {
        return modrinthProject;
    }

    public void setModrinthProject(ModrinthProject modrinthProject) {
        this.modrinthProject = modrinthProject;
    }

    public CurseForgeMatch getCurseForgeMatch() {
        return curseForgeMatch;
    }

    public void setCurseForgeMatch(CurseForgeMatch curseForgeMatch) {
        this.curseForgeMatch = curseForgeMatch;
    }

    public Classification getClassification() {
        return classification;
    }

    public void setClassification(Classification classification) {
        this.classification = classification;
    }

    public String getModrinthReason() {
        return modrinthReason;
    }

    public void setModrinthReason(String modrinthReason) {
        this.modrinthReason = modrinthReason;
    }

    public String getCurseForgeReason() {
        return curseForgeReason;
    }

    public void setCurseForgeReason(String curseForgeReason) {
        this.curseForgeReason = curseForgeReason;
    }

    public String getScanError() {
        return scanError;
    }

    public void setScanError(String scanError) {
        this.scanError = scanError;
    }
}
