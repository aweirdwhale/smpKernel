package xyz.aweirdwhale.utils;

import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class Request {

    // Constante pour le délai maximum de connexion
    private static final int DELAY = 10000;

    /**
     * Connexion à l'URL
     * @param address l'adresse à laquelle on se connecte
     * @param request requête JSON
     * @param httpMethod la méthode HTTP utilisée
     * @return la connexion à l'URL
     * @throws URISyntaxException si l'adresse est invalide
     * @throws IOException si une erreur d'entrée/sortie se produit
     * @throws IllegalArgumentException si les paramètres sont invalides
     */
    public static HttpURLConnection getHttpURLConnection(String address, String request, String httpMethod)
            throws IOException, URISyntaxException {

        // Validation des paramètres
        if (address == null || address.isBlank()) {
            throw new IllegalArgumentException("L'adresse ne peut pas être nulle ou vide.");
        }
        if (request == null || request.isBlank()) {
            throw new IllegalArgumentException("La requête ne peut pas être nulle ou vide.");
        }
        if (httpMethod == null || httpMethod.isBlank()) {
            throw new IllegalArgumentException("La méthode HTTP ne peut pas être nulle ou vide.");
        }

        // Conversion de l'adresse en URI et URL
        URI uri = new URI(address);
        URL url = uri.toURL();

        // Ouverture de la connexion
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();

        // Configuration de la requête
        connection.setRequestMethod(httpMethod);
        connection.setRequestProperty("Content-Type", "application/json");
        connection.setDoOutput(true);
        connection.setConnectTimeout(DELAY);
        connection.setReadTimeout(DELAY);

        // Envoi du JSON
        try (OutputStream os = connection.getOutputStream()) {
            byte[] input = request.getBytes(StandardCharsets.UTF_8);
            os.write(input, 0, input.length);
        }

        return connection;
    }
}