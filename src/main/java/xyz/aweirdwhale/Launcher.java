package xyz.aweirdwhale;

import xyz.aweirdwhale.intall.ClassPathsGenerator;
import xyz.aweirdwhale.intall.GetFiles;
import xyz.aweirdwhale.utils.Downloader;
import xyz.aweirdwhale.utils.SetUpDirs;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import static xyz.aweirdwhale.utils.Downloader.downloadFile;

public class Launcher {

    public static void setUp(String username, String mxRam, String mnRam) {
        String dir = SetUpDirs.getGameDirectory();
        File gameDir = new File(dir);
        System.out.println(gameDir.getAbsolutePath());

        // Placeholder for downloading versions
        downloadVersions(dir);

        GetFiles installer = new GetFiles();
        installer.run(dir);

        // Placeholder for deleting ASM
        GetFiles.deleteThisFuckingAsm(dir);

        // Placeholder for downloading assets
        downloadAssets(dir);

        // Download server files
        Path serversDatPath = Paths.get(dir, "servers.dat");
        downloadFile("http://217.154.9.109:6969/public/servers.dat", serversDatPath.toString(), false);

        Path serversDatOldPath = Paths.get(dir, "servers.dat_old");
        downloadFile("http://217.154.9.109:6969/public/servers.dat_old", serversDatOldPath.toString(), false);

        // Placeholder for downloading mods
        downloadMods(dir);

        // Generate classpath
        Path path = Paths.get(dir, "classpath.txt");
        ClassPathsGenerator.generateClassPath(Paths.get(dir, "libraries").toString(), path.toString());

        // Remove specific library for Windows
        String os = System.getProperty("os.name").toLowerCase();
        if (os.contains("win")) {
            ClassPathsGenerator.removeLibraryFromClassPath(
                    path.toString(),
                    ";" + Paths.get(dir, "libraries", "org/ow2/asm/asm/9.6/asm-9.6.jar")
            );
        }

        // Launch Minecraft
        launchMinecraft(mxRam, mnRam, path.toString(), username, dir);
    }

    public static void launchMinecraft(String maxRam, String minRam, String classPaths, String username, String gameDir) {
        try {
            List<String> command = new ArrayList<>();
            command.add("java");
            command.add("-Xmx" + maxRam + "G");
            command.add("-Xms" + minRam + "G");
            command.add("-cp");

            String classpathContent = Files.readString(Paths.get(classPaths), StandardCharsets.UTF_8);
            String os = System.getProperty("os.name").toLowerCase();
            String classpath = gameDir + "/versions/1.20.1-forge/1.20.1-forge.jar" +
                    (os.contains("win") ? ";" : ":") +
                    classpathContent.trim();
            command.add(classpath);

            command.add("net.minecraft.launchwrapper.Launch");
            command.add("--username");
            command.add(username);
            command.add("--version");
            command.add("1.20.1-forge");
            command.add("--gameDir");
            command.add(gameDir);
            command.add("--assetsDir");
            command.add(Paths.get(gameDir, "assets").toString());
            command.add("--assetIndex");
            command.add("1.20");
            command.add("--accessToken");
            command.add("SNCF"); // Externalize this in a configuration file
            command.add("--tweakClass");
            command.add("net.minecraftforge.fml.common.launcher.FMLTweaker");

            ProcessBuilder builder = new ProcessBuilder(command);
            builder.inheritIO();
            Process process = builder.start();
            process.waitFor();
        } catch (InterruptedException | IOException error) {
            System.err.println("Erreur lors du lancement de Minecraft : " + error.getMessage());
            error.printStackTrace();
        }
    }

    // Placeholder for downloadVersions
    private static void downloadVersions(String dir) {
        System.out.println("[Placeholder] Downloading versions to: " + dir);
    }

    // Placeholder for downloadAssets
    private static void downloadAssets(String dir) {
        System.out.println("[Placeholder] Downloading assets to: " + dir);
    }

    // Placeholder for downloadMods
    private static void downloadMods(String dir) {
        System.out.println("[Placeholder] Downloading mods to: " + dir);
    }
}