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
     * Gènere une liste exhaustive des libs dont minecraft à besoin.
     * @param directory location in the pc
     * @param outputFile se qui a etait crée.
     */
    public static void generateClassPath(String directory, String outputFile){
        try {
            // Trouver tous les fichiers .jar dans le répertoire donné
            List<String> jarFiles = Files.walk(Paths.get(directory))
                    .map(Path::toString)
                    .filter(string -> string.endsWith(".jar"))
                    .collect(Collectors.toList());

            // Construire le classpath en joignant les chemins avec ":" pour unix et ; pour windows
            String classpath;
            String os = System.getProperty("os.name").toLowerCase();

            if (os.contains("win")) {
                classpath = String.join(";", jarFiles);
            } else {
                classpath = String.join(":", jarFiles);
            }

            // Écrire le résultat dans un fichier
            Files.write(Paths.get(outputFile), classpath.getBytes());

            System.out.println("Classpath généré avec succès !");
            System.out.println("Lancement du jeu ...");
        } catch (IOException _) {
        }
    }

    public static void removeLibraryFromClassPath(String classPathFile, String libraryToRemove) {
        try {
            // Lire toutes les lignes du fichier
            List<String> lines = Files.readAllLines(Paths.get(classPathFile));

            // Filtrer les lignes pour supprimer celle contenant le chemin à enlever
            List<String> updatedLines = lines.stream()
                    .filter(line -> !line.equals(libraryToRemove))
                    .collect(Collectors.toList());

            // Réécrire le fichier sans la ligne supprimée
            Files.write(Paths.get(classPathFile), updatedLines, StandardOpenOption.TRUNCATE_EXISTING, StandardOpenOption.WRITE);


        } catch (IOException _) {

        }
    }


}
