package net.pl3x.map.configuration;

import net.pl3x.map.Pl3xMap;

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

    public static void reload() {
        CONFIG.reload(Pl3xMap.getInstance().getDataFolder().toPath().resolve("config.yml"), Config.class);
    }
}
