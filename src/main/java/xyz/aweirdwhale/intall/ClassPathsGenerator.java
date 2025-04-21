package xyz.aweirdwhale.intall;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.List;
import java.util.stream.Collectors;

public class ClassPathsGenerator {

    /**
     * Génère une liste exhaustive des libs dont Minecraft a besoin.
     * @param directory Répertoire contenant les fichiers .jar.
     * @param outputFile Fichier de sortie pour le classpath généré.
     */
    public static void generateClassPath(String directory, String outputFile) {
        if (directory == null || directory.isBlank()) {
            throw new IllegalArgumentException("Le répertoire ne peut pas être nul ou vide.");
        }
        if (outputFile == null || outputFile.isBlank()) {
            throw new IllegalArgumentException("Le fichier de sortie ne peut pas être nul ou vide.");
        }

        try {
            // Trouver tous les fichiers .jar dans le répertoire donné
            List<String> jarFiles = Files.walk(Paths.get(directory))
                    .filter(Files::isRegularFile)
                    .map(Path::toString)
                    .filter(string -> string.endsWith(".jar"))
                    .collect(Collectors.toList());

            if (jarFiles.isEmpty()) {
                System.out.println("Aucun fichier .jar trouvé dans le répertoire : " + directory);
                return;
            }

            // Construire le classpath en joignant les chemins avec ":" pour Unix et ";" pour Windows
            String os = System.getProperty("os.name").toLowerCase();
            String classpath = os.contains("win") ? String.join(";", jarFiles) : String.join(":", jarFiles);

            // Écrire le résultat dans un fichier
            Files.write(Paths.get(outputFile), classpath.getBytes(), StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);

            System.out.println("Classpath généré avec succès dans le fichier : " + outputFile);
        } catch (IOException error) {
            System.err.println("Erreur lors de la génération du classpath : " + error.getMessage());
        }
    }

    /**
     * Supprime une bibliothèque spécifique du fichier classpath.
     * @param classPathFile Fichier contenant le classpath.
     * @param libraryToRemove Chemin de la bibliothèque à supprimer.
     */
    public static void removeLibraryFromClassPath(String classPathFile, String libraryToRemove) {
        if (classPathFile == null || classPathFile.isBlank()) {
            throw new IllegalArgumentException("Le fichier classpath ne peut pas être nul ou vide.");
        }
        if (libraryToRemove == null || libraryToRemove.isBlank()) {
            throw new IllegalArgumentException("La bibliothèque à supprimer ne peut pas être nulle ou vide.");
        }

        try {
            // Lire toutes les lignes du fichier
            List<String> lines = Files.readAllLines(Paths.get(classPathFile));

            // Filtrer les lignes pour supprimer celle contenant le chemin à enlever
            List<String> updatedLines = lines.stream()
                    .filter(line -> !line.equals(libraryToRemove))
                    .collect(Collectors.toList());

            // Réécrire le fichier sans la ligne supprimée
            Files.write(Paths.get(classPathFile), updatedLines, StandardOpenOption.TRUNCATE_EXISTING, StandardOpenOption.WRITE);

            System.out.println("La bibliothèque a été supprimée avec succès du fichier classpath.");
        } catch (IOException error) {
            System.err.println("Erreur lors de la suppression de la bibliothèque du classpath : " + error.getMessage());
        }
    }
}