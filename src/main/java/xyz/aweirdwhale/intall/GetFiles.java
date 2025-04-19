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

    //LIBS (globalement attroce et spaghettis mais là j'ai pas le temps)
    public void run(String path) {
        // Check if JSON files exist
        for (String jsonPath : Infos.JSON_PATHS) {
            if (!new File(path+jsonPath).exists()) {
                System.out.println("Erreur: Le fichier " + path+jsonPath + " n'existe pas.");
                return;
            }
        }

        // Get all library URLs from JSON files
        List<String[]> urls = new ArrayList<>();
        Map<String, String> versionsMap = new HashMap<>();
        for (String jsonPath : Infos.JSON_PATHS) {
            String[] result = getLibrariesFromJson(path+jsonPath);
            urls.addAll(Arrays.stream(result[0].split(";")).map(s -> s.split(",")).toList());
            versionsMap.putAll(parseVersionsMap(result[1]));
        }

        // Remove old versions of libraries
        removeOldVersions(versionsMap, path);

        // Download each library
        for (String[] urlPath : urls) {
            downloadLibrary(urlPath[0], urlPath[1], path);
        }

        System.out.println("[ok] Téléchargement terminé !");
    }

    /**
     * Cherche l'ensembles des librairie json.
     * @param jsonPath chemin du fichier json.
     * @return ensemble des librairies trouver dans le dossier.
     */
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

                JSONObject downloads = lib.optJSONObject("downloads");
                if (downloads != null) {
                    JSONObject artifact = downloads.optJSONObject("artifact");
                    if (artifact != null && artifact.has("url")) {
                        urls.append(artifact.getString("url")).append(",").append(artifact.optString("path", artifact.getString("url").split("/")[artifact.getString("url").split("/").length - 1])).append(";");
                    }

                    JSONObject classifiers = downloads.optJSONObject("classifiers");
                    if (classifiers != null) {
                        for (String key : classifiers.keySet()) {
                            JSONObject nativeFile = classifiers.getJSONObject(key);
                            if (nativeFile.has("url")) {
                                urls.append(nativeFile.getString("url")).append(",").append(nativeFile.optString("path", nativeFile.getString("url").split("/")[nativeFile.getString("url").split("/").length - 1])).append(";");
                            }
                        }
                    }
                }
            }
        }  catch (IOException _) {
        }

        return new String[]{urls.toString(), versionsMap.toString()};
    }

    /**
     * @return différente version de la Map.
     */
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
        File librariesDir = new File(path+Infos.LIBRARIES_DIR);
        if (librariesDir.exists() && librariesDir.isDirectory()) {
            for (File file : Objects.requireNonNull(librariesDir.listFiles())) {
                if (file.isFile()) {
                    Matcher matcher = Pattern.compile("(.+)-(\\d+\\.\\d+\\.\\d+).*\\.jar").matcher(file.getName()); // Ew regex
                    if (matcher.matches()) {
                        String libKey = matcher.group(1);
                        String version = matcher.group(2);
                        if (versionsMap.containsKey(libKey) && version.compareTo(versionsMap.get(libKey)) < 0) {
                            System.out.println("[X] Suppression de l'ancienne version: " + file.getPath());
                            file.delete();
                        }
                    }
                }
            }
        }
    }

    /**
     * IDK wtf I have to delete this one manually
     * **/
    public static void deleteThisFuckingAsm(String path){
        String filePath = path + "/libraries/org/ow2/asm/asm/9.6/asm-9.6.jar";
        File file = new File(filePath);

        if (file.exists()) {
            if (file.delete()) {
                System.out.println("[ok] Fichier supprimé avec succès !");
            } else {
                System.out.println("[bad] Impossible de supprimer le fichier.");
            }
        } else {
            System.out.println("[warn] Fichier introuvable.");
        }

    }

    private void downloadLibrary(String urlString, String filePath, String path)  {
        File file = new File(path+Infos.LIBRARIES_DIR + filePath);
        if (file.exists()) {
            System.out.println("✔ Librairie déjà présente : " + file.getName());
            return;
        }

        try {
            File parentDir = file.getParentFile();
            if (!parentDir.exists()) {
                parentDir.mkdirs();
            }

            System.out.println("⌛ Téléchargement de " + urlString + " à " + file.getPath() + "...");

            URI uri = new URI(urlString);
            URL url = uri.toURL(); //URI
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

                System.out.println("[ok] Fichier téléchargé : " + file.getPath());
            } else {
                System.out.println("[bad] Erreur (" + connection.getResponseCode() + ") lors du téléchargement de " + urlString);
            }
        } catch (IOException | URISyntaxException e) {
        }

    }

    //VERSIONS
    public static void downloadVersions(String path) {
        downloadFile(Infos.FABRIC_URL, path + "/versions/fabric-loader/fabric.jar", false);
        downloadFile(Infos.FABRIC_JSON_URL, path + "/versions/fabric-loader/fabric.json", false);
        downloadFile(Infos.MINECRAFT_URL, path + "/versions/1.21.4/1.21.4.jar", false);
        downloadFile(Infos.MINECRAFT_JSON_URL, path + "/versions/1.21.4/1.21.4.json", false);

        downloadAssets(path);
    }

    //ASSETS
    public static void downloadAssets(String path)  {
        String versionJsonPath = path + "/versions/1.21.4/1.21.4.json";
        try {

            System.out.println("[...] Téléchargement des assets ...");

            String jsonContent = new String(Files.readAllBytes(Paths.get(versionJsonPath)));
            JSONObject versionJson = new JSONObject(jsonContent);

            JSONObject assetIndex = versionJson.getJSONObject("assetIndex");
            String assetIndexUrl = assetIndex.getString("url");
            String assetIndexId = assetIndex.getString("id");

            String assetIndexPath = path + "/assets/indexes/" + assetIndexId + ".json";
            downloadFile(assetIndexUrl, assetIndexPath, false);


            // Télécharger l'asset index dans un répertoire temporaire
            String assetsDir = path + "/assets/indexes";
            new File(assetsDir).mkdirs();
            String assetIndexFilePath = assetsDir + "/" + assetIndexId + ".json";
            downloadFile(assetIndexUrl, assetIndexFilePath, false);

            // Lire et parser l'asset index
            String assetIndexContent = new String(Files.readAllBytes(Paths.get(assetIndexFilePath)));
            JSONObject assetIndexJson = new JSONObject(assetIndexContent);
            JSONObject objects = assetIndexJson.getJSONObject("objects");

            // Télécharger chaque asset
            String objectsDirPath = path + "/assets/objects";
            new File(objectsDirPath).mkdirs();

            Iterator<String> keys = objects.keys();
            while (keys.hasNext()) {
                String assetName = keys.next();
                JSONObject assetInfo = objects.getJSONObject(assetName);
                String hash = assetInfo.getString("hash");
                String assetDownloadUrl = Infos.ASSET_BASE_URL + "/" + hash.substring(0, 2) + "/" + hash;
                String assetLocalDirPath = objectsDirPath + "/" + hash.substring(0, 2);
                new File(assetLocalDirPath).mkdirs();
                String assetLocalPath = assetLocalDirPath + "/" + hash;

                // Vérifier l'intégrité avant téléchargement
                File assetFile = new File(assetLocalPath);
                if (assetFile.exists()) {
                    continue;
                }

                // Téléchargement de l'asset
                downloadFile(assetDownloadUrl, assetLocalPath, false);
            }
        } catch (Exception _) {
            System.out.println("[X] Erreur lors du téléchargement des assets");
        }
    }

    // MODS
    public static void downloadMods(String modDir) {

        try {
            URL newUrl = new URL(Infos.MODS);

            HttpURLConnection connection = (HttpURLConnection) newUrl.openConnection();
            connection.setRequestMethod("GET");

            if (connection.getResponseCode() == 200) {
                StringBuilder jsonContent = new StringBuilder();
                try (BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
                    String line;
                    while ((line = reader.readLine()) != null) {
                        jsonContent.append(line);
                    }
                }

                JSONArray mods = new JSONArray(jsonContent.toString());
                for (int i = 0; i < mods.length(); i++) {
                    String modUrl = mods.getString(i);
                    String modName = modUrl.substring(modUrl.lastIndexOf('/') + 1);
                    String modPath = modDir + "/mods/" + modName;

                    downloadFile(modUrl, modPath, false);
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


}
