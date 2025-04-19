package xyz.aweirdwhale.utils;

public class Infos {
    public static final String URL = "http://217.154.9.109";
    public static final String PORT = ":6969";
    public static final String MODS = URL + ":" + PORT + "/public/mods.json";
    public static final String VERSION = "2.0";
    public static final String KERNELVERSION = "1.0";
    public static final String[] JSON_PATHS = {
            "/versions/1.21.1/1.21.1.json",
            "/versions/forge/forge.json"
    };
    public static final String LIBRARIES_DIR = "/libraries/";
    public static final String[] MAVEN_REPOSITORIES = {
            "https://libraries.minecraft.net/",
            "https://maven.fabricmc.net/"
    };

    public static final String FABRIC_URL = URL + ":" + PORT + "/public/forge/forge.jar";
    public static final String FABRIC_JSON_URL = URL + ":" + PORT + "/public/forge/forge.json";
    public static final String MINECRAFT_URL = URL + ":" + PORT + "/public/1.21.1/1.21.1.jar";
    public static final String MINECRAFT_JSON_URL = URL + ":" + PORT + "/public/1.21.1/1.21.1.json";


    public static final String ASSET_BASE_URL = "https://resources.download.minecraft.net";
}
