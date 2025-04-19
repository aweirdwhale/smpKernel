package xyz.aweirdwhale.utils;

import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class Request {

    /**
     * Connection à l'URL
     * @param address l'adresse à la quelle on se connecte
     * @param request requéte
     * @param httpMethode la méthode utiliser
     * @return la connection a l'URL
     * @throws URISyntaxException erreur
     * @throws IOException erreur
     */
    public static HttpURLConnection getHttpURLConnection(String address, String request, String httpMethode) throws IOException, URISyntaxException {
        // Handle URIs
        URI uri = new URI(address);
        URL url = uri.toURL();

        HttpURLConnection connection = (HttpURLConnection) url.openConnection();

        // Configure the request
        connection.setRequestMethod(httpMethode);
        connection.setRequestProperty("Content-Type", "application/json");
        connection.setDoOutput(true);

        // Maximum connection delay in ms
        int DELAY = 10000;
        connection.setConnectTimeout(DELAY);
        connection.setReadTimeout(DELAY);

        // Send the JSON
        try (OutputStream os = connection.getOutputStream()) {
            byte[] input = request.getBytes(StandardCharsets.UTF_8);
            os.write(input, 0, input.length);
        }
        return connection;
    }
}
