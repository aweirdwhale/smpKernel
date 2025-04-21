package xyz.aweirdwhale.utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.file.Files;
import java.nio.file.Path;

public class Downloader {
    /**
     * Télécharge un fichier depuis une URL donnée.
     * @param fileUrl URL du fichier à télécharger.
     * @param savePath Chemin où stocker le fichier.
     * @param force Si `true`, force le téléchargement même si le fichier existe déjà.
     */
    public static void downloadFile(String fileUrl, String savePath, boolean force) {
        // Validation des paramètres
        if (fileUrl == null || fileUrl.isBlank()) {
            throw new IllegalArgumentException("L'URL du fichier ne peut pas être nulle ou vide.");
        }
        if (savePath == null || savePath.isBlank()) {
            throw new IllegalArgumentException("Le chemin de sauvegarde ne peut pas être nul ou vide.");
        }

        File file = new File(savePath);

        // Évite de télécharger plusieurs fois si le fichier existe déjà
        if (!force && file.exists()) {
            System.out.println("[ok] Le fichier existe déjà, on passe au suivant...");
            return;
        }

        try {
            URI uri = URI.create(fileUrl);
            URL url = uri.toURL();

            Path parentDir = file.toPath().getParent();

            // Crée le dossier parent s'il n'existe pas
            if (parentDir != null && Files.notExists(parentDir)) {
                Files.createDirectories(parentDir);
                System.out.println("[ok] Création du répertoire : " + parentDir);
            }

            // Téléchargement du fichier
            try (ReadableByteChannel channel = Channels.newChannel(url.openStream());
                 FileOutputStream fos = new FileOutputStream(file)) {
                fos.getChannel().transferFrom(channel, 0, Long.MAX_VALUE);
                System.out.println("[ok] Le fichier a bien été téléchargé : " + file.getPath());
            }

        } catch (IOException e) {
            System.err.println("[bad] Erreur lors du téléchargement de " + fileUrl + " : " + e.getMessage());
        }
    }
}