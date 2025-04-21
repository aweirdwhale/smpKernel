package xyz.aweirdwhale;

import xyz.aweirdwhale.utils.Hasher;

import java.security.NoSuchAlgorithmException;

import static xyz.aweirdwhale.Launcher.setUp;
import static xyz.aweirdwhale.login.Login.login;

public class Main {
    public static void main(String[] args) {
        /*
        args[0] = username
        args[1] = password
        args[2] = maxRam
        args[3] = minRam
        */

        if (args.length < 4) {
            System.err.println("Usage: java -jar app.jar <username> <password> <maxRam> <minRam>");
            return;
        }

        String username = args[0];
        String password = args[1];
        String maxRam = args[2];
        String minRam = args[3];

        try {
            // Hash the password
            String hashedPassword = Hasher.hash(password);

            // Attempt login
            int responseCode = login(username, hashedPassword);

            if (responseCode == 200) {
                System.out.println("[OK] Login successful! Setting up the game...");
                setUp(username, maxRam, minRam);
            } else {
                System.err.println("[Error] Login failed! Please check your credentials.");
            }
        } catch (NoSuchAlgorithmException e) {
            System.err.println("[Error] Failed to hash the password: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("[Error] An unexpected error occurred: " + e.getMessage());
            e.printStackTrace();
        }
    }
}