package xyz.aweirdwhale.intall;

import xyz.aweirdwhale.utils.Downloader;
import xyz.aweirdwhale.utils.Infos;
import xyz.aweirdwhale.utils.SetUpDirs;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Installer {

    public void install(String username, String mxRam, String mnRam) {
        String gameDir = SetUpDirs.getGameDirectory();
        System.out.println("[Installer] Game directory: " + gameDir);

        // Ensure the game directory exists
        File gameDirFile = new File(gameDir);
        if (!gameDirFile.exists() && !gameDirFile.mkdirs()) {
            System.err.println("[Error] Failed to create game directory: " + gameDir);
            return;
        }

        try {
            // Step 1: Download game files
            downloadGameFiles(gameDir);

            // Step 2: Download mods
            downloadMods(gameDir);

            // Step 3: Download assets
            downloadAssets(gameDir);

            // Step 4: Generate classpath
            generateClassPath(gameDir);

            System.out.println("[Installer] Installation completed successfully!");
        } catch (Exception e) {
            System.err.println("[Error] Installation failed: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void downloadGameFiles(String gameDir) {
        System.out.println("[Installer] Downloading game files...");
        try {
            Downloader.downloadFile(Infos.MINECRAFT_URL, Paths.get(gameDir, "versions/1.20.1/1.20.1.jar").toString(), false);
            Downloader.downloadFile(Infos.MINECRAFT_JSON_URL, Paths.get(gameDir, "versions/1.20.1/1.20.1.json").toString(), false);
            Downloader.downloadFile(Infos.FABRIC_URL, Paths.get(gameDir, "versions/forge/forge.jar").toString(), false);
            Downloader.downloadFile(Infos.FABRIC_JSON_URL, Paths.get(gameDir, "versions/forge/forge.json").toString(), false);
        } catch (Exception e) {
            System.err.println("[Error] Failed to download game files: " + e.getMessage());
        }
    }

    private void downloadMods(String gameDir) {
        System.out.println("[Installer] Downloading mods...");
        try {
            String modsJsonPath = Paths.get(gameDir, "mods.json").toString();
            Downloader.downloadFile(Infos.MODS, modsJsonPath, true);

            GetFiles getFiles = new GetFiles();
            getFiles.run(gameDir);
        } catch (Exception e) {
            System.err.println("[Error] Failed to download mods: " + e.getMessage());
        }
    }

    private void downloadAssets(String gameDir) {
        System.out.println("[Installer] Downloading assets...");
        try {
            Path assetsDir = Paths.get(gameDir, "assets");
            if (!assetsDir.toFile().exists() && !assetsDir.toFile().mkdirs()) {
                System.err.println("[Error] Failed to create assets directory: " + assetsDir);
                return;
            }

            // Example: Download a specific asset (adjust as needed)
            Downloader.downloadFile(Infos.ASSET_BASE_URL + "/index/1.20.json", assetsDir.resolve("1.20.json").toString(), false);
        } catch (Exception e) {
            System.err.println("[Error] Failed to download assets: " + e.getMessage());
        }
    }

    private void generateClassPath(String gameDir) {
        System.out.println("[Installer] Generating classpath...");
        try {
            Path librariesDir = Paths.get(gameDir, "libraries");
            if (!librariesDir.toFile().exists()) {
                System.err.println("[Error] Libraries directory does not exist: " + librariesDir);
                return;
            }

            Path classpathFile = Paths.get(gameDir, "classpath.txt");
            ClassPathsGenerator.generateClassPath(librariesDir.toString(), classpathFile.toString());
        } catch (Exception e) {
            System.err.println("[Error] Failed to generate classpath: " + e.getMessage());
        }
    }
}