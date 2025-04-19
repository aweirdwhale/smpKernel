package xyz.aweirdwhale;

import xyz.aweirdwhale.utils.Hasher;

import java.security.NoSuchAlgorithmException;

import static xyz.aweirdwhale.Launcher.setUp;
import static xyz.aweirdwhale.login.Login.login;

public class Main {
    public static void main(String[] args) throws NoSuchAlgorithmException {
        /*
        args[0] = username
        args[1] = maxRam
        args[2] = minRam
        */
//        GESTION DU LOGIN EN RUST

//        String hashed = Hasher.hash(args[1]);
//        int connexion = login(args[0], hashed);
//
//        if (connexion == 200) {
//            setUp(args[0], args[2], args[3]);
//        } else {
//            System.out.println("⤫ Ré-essaie, si tu as oublié tes identifiants, ping moi sur discord");
//        }

        // Appeler launcher.setUp avec Username, maxRam, minRam
        setUp(args[0], args[1], args[2]);
    }
}