package xyz.aweirdwhale.utils;

import java.io.File;
import java.nio.file.Paths;

public class SetUpDirs {

    public static String getGameDirectory() {
        String path = createGameDirectory();

        // Crée les sous-dossiers
        createSubDirectories(path, "mods");
        createSubDirectories(path, "assets");
        createSubDirectories(path, "libraries");
        createSubDirectories(path, "versions");
        createSubDirectories(Paths.get(path, "versions").toString(), "1.21.1");
        createSubDirectories(Paths.get(path, "versions").toString(), "forge");

        return path;
    }

    public static String createGameDirectory() {
        String home = System.getProperty("user.home"); // Root
        String os = System.getProperty("os.name").toLowerCase(); // Diff Unix / Windows

        String path;

        if (os.contains("win")) {
            path = System.getenv("APPDATA") + File.separator + ".smp2ix";
        } else {
            path = home + File.separator + ".smp2ix";
        }

        File gameDir = new File(path);
        if (!gameDir.exists() && !gameDir.mkdirs()) {
            throw new RuntimeException("Failed to create game directory: " + path);
        }

        return path;
    }

    public static void createSubDirectories(String parent, String child) {
        File dir = new File(parent, child);

        if (!dir.exists() && !dir.mkdirs()) {
            throw new RuntimeException("Failed to create subdirectory: " + dir.getAbsolutePath());
        }

        System.out.println("Created: " + dir.getAbsolutePath());
    }
}