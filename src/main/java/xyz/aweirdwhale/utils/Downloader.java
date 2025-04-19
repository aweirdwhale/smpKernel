package xyz.aweirdwhale.utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;

public class Downloader {
    /*
    * Downloads files from given URL
    * */

    /**
     * downloadFile :
     * @param fileUrl : Url du fichier à télécharger;
     * @param savePath : Chemin où stocker le fichier;
     * **/
    public static void downloadFile(String fileUrl, String savePath, boolean force) {
        File file = new File(savePath);

        // Pour éviter de télécharger plusieurs fois (long et inutile)
        if (!force) {
            if (file.exists()) { // lazy comparaison a pas l'air de marcher sinon
                System.out.println("[ok] Le fichier existe déjà, on passe au suivant...");
                return;
            }

        }

        try {
            URI uri = URI.create(fileUrl);
            URL url = uri.toURL();

            File parentDir = file.getParentFile();

            // crée le dossier parent si il n'existe pas
            if (!parentDir.exists()) {
                parentDir.mkdirs();
                System.out.println("[ok] Création du répertoire : " + parentDir.getPath());
            }

            try (ReadableByteChannel channel = Channels.newChannel(url.openStream());
                 FileOutputStream fos = new FileOutputStream(savePath)) {
                fos.getChannel().transferFrom(channel, 0, Long.MAX_VALUE);
                System.out.println("[ok] Le fichier a bien été téléchargé ! " + file.getPath());
            }

        } catch (IOException e) {
            System.out.println("[bad] Erreur lors du téléchargement de " + fileUrl + " : " + e.getMessage());
        }
    }
}
