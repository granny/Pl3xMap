package net.pl3x.map.configuration;

import java.io.File;
import java.util.List;

public class Config extends AbstractConfig {
    @Key("settings.debug-mode")
    public static boolean DEBUG_MODE = false;
    @Key("settings.language-file")
    public static String LANGUAGE_FILE = "lang-en.yml";

    @Key("settings.web-directory.path")
    public static String WEB_DIR = "web";
    @Key("settings.web-directory.read-only")
    public static boolean WEB_DIR_READONLY = false;

    @Key("settings.internal-webserver.enabled")
    public static boolean HTTPD_ENABLED = true;
    @Key("settings.internal-webserver.bind")
    public static String HTTPD_BIND = "0.0.0.0";
    @Key("settings.internal-webserver.port")
    public static int HTTPD_PORT = 8080;

    private static final Config CONFIG = new Config();

    public static void reload(File dir) {
        CONFIG.setHeader(List.of(
                "This is the main configuration file for Pl3xMap.",
                "",
                "If you need help with the configuration or have any",
                "questions related to Pl3xMap, join us in our Discord",
                "",
                "Discord: https://discord.gg/nhGzEkwXQX",
                "Wiki: https://github.com/BillyGalbreath/Pl3xMap2/wiki"
        ));
        CONFIG.reload(new File(dir, "config.yml"), Config.class);
    }
}
