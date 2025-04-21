package xyz.aweirdwhale.login;

import xyz.aweirdwhale.utils.Infos;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URISyntaxException;

import static xyz.aweirdwhale.utils.Request.getHttpURLConnection;

public class Login {

    /**
     * Envoie une requête et selon la réponse, stocke le pseudo et la date de connexion.
     * @param username Nom d'utilisateur.
     * @param hashed Mot de passe haché.
     * @return Code de réponse HTTP ou -1 en cas d'erreur.
     */
    public static int login(String username, String hashed) {
        String requestBody = "{\"user\":\"" + username + "\",\"mdp\":\"" + hashed + "\"}";

        try {
            String target = "/login";
            String method = "POST";
            String url = Infos.URL + ":" + Infos.PORT + target;

            HttpURLConnection connection = getHttpURLConnection(url, requestBody, method);
            return connection.getResponseCode();
        } catch (IOException | URISyntaxException error) {
            System.err.println("[bad] Erreur de connexion (pas de wifi ou serveur éteint, demande @Aweirdwhale en cas de doute)");
            error.printStackTrace(); // Optionally log the stack trace for debugging
        }

        return -1; // Return -1 to indicate an error
    }
}