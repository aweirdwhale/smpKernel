package xyz.aweirdwhale.login;

import xyz.aweirdwhale.utils.Infos;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URISyntaxException;

import static xyz.aweirdwhale.utils.Request.getHttpURLConnection;

public class Login {

    /*
     * envoie une requette et selon la réponse, store le pseudo, la date de connection
     * return true si le jeu peut se lancer
     * return false sinon
     */


    public static int login(String username, String hashed) {
        String request_body = "{\"user\":\"" + username + "\",\"mdp\":\"" + hashed + "\"}";

        try {
            String TARGET = "/login";
            String PORT = Infos.PORT;
            String METHOD = "POST";
            HttpURLConnection connection = getHttpURLConnection(Infos.URL + PORT + TARGET, request_body, METHOD);
            return connection.getResponseCode();
        } catch (IOException | URISyntaxException _) {
            System.out.println("[bad] Erreur de connexion (pas de wifi ou serv éteint demande @Aweirdwhale en cas de doute)");
        }

        return 0;
    }
}
