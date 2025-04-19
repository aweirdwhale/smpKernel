package xyz.aweirdwhale;

import xyz.aweirdwhale.intall.ClassPathsGenerator;
import xyz.aweirdwhale.intall.GetFiles;
import xyz.aweirdwhale.utils.Downloader;
import xyz.aweirdwhale.utils.SetUpDirs;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import static xyz.aweirdwhale.intall.GetFiles.*;
import static xyz.aweirdwhale.utils.Downloader.downloadFile;

public class Launcher {
    // Lance le jeu avec les paramètres demandés
    public static void setUp(String username, String mxRam, String mnRam) {

        // crée l'arborescence
        String dir = SetUpDirs.getGameDirectory();
        File gameDir = new File(dir);
        System.out.println(gameDir.getAbsolutePath());

        // télécharge les versions
        Downloader Downloader = new Downloader();
        downloadVersions(dir);


        // télécharge les libs
        GetFiles installer = new GetFiles();
        installer.run(dir);


        // systemes unix
        GetFiles.deleteThisFuckingAsm(dir);

        // télécharge les assets
        downloadAssets(dir);

        downloadFile("http://217.154.9.109:6969/public/servers.dat", dir+"/servers.dat", false);
        downloadFile("http://217.154.9.109:6969/public/servers.dat_old", dir+"/servers.dat", false);


        // télécharge les mods
        downloadMods(dir);


        // générer le ClassPath
        ClassPathsGenerator.generateClassPath(dir + "/libraries", dir + "/classpath.txt");

        // supprime l'asm chiant pour windows
        String os = System.getProperty("os.name").toLowerCase();

        if (os.contains("win")) {
            ClassPathsGenerator.removeLibraryFromClassPath(dir + "/classpath.txt", ";" + dir + "/libraries/org/ow2/asm/asm/9.6/asm-9.6.jar");
        }

        //lance le jeu
        launchMinecraft(mxRam, mnRam, dir + "/classpath.txt", username, dir);


    }


    public static void launchMinecraft(String maxRam, String minRam, String ClassPaths, String username, String gameDir) {
        try {

            List<String> command = new ArrayList<>();
            command.add("java");
            command.add("-Xmx" + maxRam + "G");
            command.add("-Xms" + minRam + "G");
            command.add("-cp");

// Charger le contenu du fichier classpath
            String classpathContent = new String(Files.readAllBytes(Paths.get(ClassPaths)));
            String classpath;
            String os = System.getProperty("os.name").toLowerCase();

// Ajout du forge.jar au classpath
            if (os.contains("win")) {
                classpath = gameDir + "/versions/1.21.1-forge/1.21.1-forge.jar;" + classpathContent.trim();
            } else {
                classpath = gameDir + "/versions/1.21.1-forge/1.21.1-forge.jar:" + classpathContent.trim();
            }
            command.add(classpath);

// Classe principale pour Forge
            command.add("net.minecraft.launchwrapper.Launch");

// Arguments Forge
            command.add("--username");
            command.add(username);
            command.add("--version");
            command.add("1.21.1-forge");
            command.add("--gameDir");
            command.add(gameDir);
            command.add("--assetsDir");
            command.add(gameDir + "/assets");
            command.add("--assetIndex");
            command.add("1.21");
            command.add("--accessToken");
            command.add("SNCF");

// Obligatoire pour Forge
            command.add("--tweakClass");
            command.add("net.minecraftforge.fml.common.launcher.FMLTweaker");


            ProcessBuilder builder = new ProcessBuilder(command);
            builder.inheritIO();
            Process process = builder.start();
            process.waitFor();
        } catch (InterruptedException | IOException _) {

        }
    }

}
