package xyz.aweirdwhale.intall;

import org.json.JSONArray;
import org.json.JSONObject;
import xyz.aweirdwhale.utils.Infos;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static xyz.aweirdwhale.utils.Downloader.downloadFile;

public class GetFiles {

    public void run(String path) {
        try {
            // Check if JSON files exist
            for (String jsonPath : Infos.JSON_PATHS) {
                File jsonFile = new File(path + jsonPath);
                if (!jsonFile.exists()) {
                    System.err.println("[Error] File not found: " + jsonFile.getPath());
                    return;
                }
            }

            // Get all library URLs from JSON files
            List<String[]> urls = new ArrayList<>();
            Map<String, String> versionsMap = new HashMap<>();
            for (String jsonPath : Infos.JSON_PATHS) {
                String[] result = getLibrariesFromJson(path + jsonPath);
                urls.addAll(Arrays.stream(result[0].split(";")).map(s -> s.split(",")).toList());
                versionsMap.putAll(parseVersionsMap(result[1]));
            }

            // Remove old versions of libraries
            removeOldVersions(versionsMap, path);

            // Download each library
            for (String[] urlPath : urls) {
                downloadLibrary(urlPath[0], urlPath[1], path);
            }

            System.out.println("[OK] Download completed!");
        } catch (Exception e) {
            System.err.println("[Error] An unexpected error occurred: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static void deleteThisFuckingAsm(String dir) {
        System.out.println("[Placeholder] Deleting ASM files in: " + dir);
    }

    private String[] getLibrariesFromJson(String jsonPath) {
        StringBuilder urls = new StringBuilder();
        StringBuilder versionsMap = new StringBuilder();

        try (BufferedReader reader = new BufferedReader(new FileReader(jsonPath))) {
            StringBuilder jsonContent = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                jsonContent.append(line);
            }
            JSONObject data = new JSONObject(jsonContent.toString());

            JSONArray libraries = data.getJSONArray("libraries");
            for (int i = 0; i < libraries.length(); i++) {
                JSONObject lib = libraries.getJSONObject(i);
                String name = lib.optString("name");
                String url = lib.optString("url", Infos.MAVEN_REPOSITORIES[0]);

                if (!name.isEmpty()) {
                    String[] parts = name.split(":");
                    if (parts.length == 3) {
                        String group = parts[0];
                        String artifact = parts[1];
                        String version = parts[2];
                        String libPath = group.replace('.', '/') + "/" + artifact + "/" + version + "/" + artifact + "-" + version + ".jar";
                        String fullUrl = url + libPath;
                        urls.append(fullUrl).append(",").append(libPath).append(";");

                        String key = group + "." + artifact;
                        if (!versionsMap.toString().contains(key) || version.compareTo(versionsMap.toString()) > 0) {
                            versionsMap.append(key).append(",").append(version).append(";");
                        }
                    }
                }
            }
        } catch (IOException e) {
            System.err.println("[Error] Failed to read JSON file: " + jsonPath);
        }

        return new String[]{urls.toString(), versionsMap.toString()};
    }

    private Map<String, String> parseVersionsMap(String versionsMapStr) {
        Map<String, String> versionsMap = new HashMap<>();
        String[] entries = versionsMapStr.split(";");
        for (String entry : entries) {
            String[] keyValue = entry.split(",");
            if (keyValue.length == 2) {
                versionsMap.put(keyValue[0], keyValue[1]);
            }
        }
        return versionsMap;
    }

    private void removeOldVersions(Map<String, String> versionsMap, String path) {
        File librariesDir = new File(path + Infos.LIBRARIES_DIR);
        if (librariesDir.exists() && librariesDir.isDirectory()) {
            for (File file : Objects.requireNonNull(librariesDir.listFiles())) {
                if (file.isFile()) {
                    Matcher matcher = Pattern.compile("(.+)-(\\d+\\.\\d+\\.\\d+).*\\.jar").matcher(file.getName());
                    if (matcher.matches()) {
                        String libKey = matcher.group(1);
                        String version = matcher.group(2);
                        if (versionsMap.containsKey(libKey) && version.compareTo(versionsMap.get(libKey)) < 0) {
                            System.out.println("[X] Deleting old version: " + file.getPath());
                            if (!file.delete()) {
                                System.err.println("[Error] Failed to delete file: " + file.getPath());
                            }
                        }
                    }
                }
            }
        }
    }

    private void downloadLibrary(String urlString, String filePath, String path) {
        File file = new File(path + Infos.LIBRARIES_DIR + filePath);
        if (file.exists()) {
            System.out.println("✔ Library already exists: " + file.getName());
            return;
        }

        try {
            File parentDir = file.getParentFile();
            if (!parentDir.exists() && !parentDir.mkdirs()) {
                System.err.println("[Error] Failed to create directory: " + parentDir.getPath());
                return;
            }

            System.out.println("⌛ Downloading " + urlString + " to " + file.getPath() + "...");

            URI uri = new URI(urlString);
            URL url = uri.toURL();
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");

            if (connection.getResponseCode() == 200) {
                try (InputStream in = connection.getInputStream();
                     FileOutputStream out = new FileOutputStream(file)) {
                    byte[] buffer = new byte[4096];
                    int bytesRead;
                    while ((bytesRead = in.read(buffer)) != -1) {
                        out.write(buffer, 0, bytesRead);
                    }
                }
                System.out.println("[OK] File downloaded: " + file.getPath());
            } else {
                System.err.println("[Error] Failed to download " + urlString + " (HTTP " + connection.getResponseCode() + ")");
            }
        } catch (IOException | URISyntaxException e) {
            System.err.println("[Error] Failed to download library: " + e.getMessage());
        }
    }
}