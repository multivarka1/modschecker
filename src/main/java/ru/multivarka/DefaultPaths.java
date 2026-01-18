package ru.multivarka;

import java.net.URI;
import java.nio.file.Path;
import java.nio.file.Paths;

public final class DefaultPaths {
    private DefaultPaths() {
    }

    public static Path defaultModsDir() {
        try {
            URI location = Main.class.getProtectionDomain().getCodeSource().getLocation().toURI();
            Path path = Paths.get(location);
            if (path.toString().toLowerCase().endsWith(".jar")) {
                Path parent = path.getParent();
                if (parent != null) {
                    return parent.resolve("mods");
                }
            }
        } catch (Exception ignored) {
        }
        return Paths.get("").toAbsolutePath().resolve("mods");
    }

    public static Path defaultConfigPath() {
        try {
            URI location = Main.class.getProtectionDomain().getCodeSource().getLocation().toURI();
            Path path = Paths.get(location);
            if (path.toString().toLowerCase().endsWith(".jar")) {
                Path parent = path.getParent();
                if (parent != null) {
                    return parent.resolve("modschecker.properties");
                }
            }
        } catch (Exception ignored) {
        }
        return Paths.get("").toAbsolutePath().resolve("modschecker.properties");
    }
}
