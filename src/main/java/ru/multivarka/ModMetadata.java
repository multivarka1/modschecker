package ru.multivarka;

public class ModMetadata {
    private final String id;
    private final String name;
    private final String version;
    private final MetadataSource source;

    public ModMetadata(String id, String name, String version, MetadataSource source) {
        this.id = id;
        this.name = name;
        this.version = version;
        this.source = source;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getVersion() {
        return version;
    }

    public MetadataSource getSource() {
        return source;
    }
}
