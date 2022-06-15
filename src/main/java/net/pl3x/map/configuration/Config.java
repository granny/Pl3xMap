package net.pl3x.map.configuration;

import net.pl3x.map.Pl3xMap;

public class Config extends AbstractConfig {
    @Key("settings.debug-mode")
    public static boolean DEBUG_MODE;
    @Key("settings.language-file")
    public static String LANGUAGE_FILE;

    @Key("settings.web-directory.path")
    public static String WEB_DIR;
    @Key("settings.web-directory.read-only")
    public static boolean WEB_DIR_READONLY;

    @Key("settings.internal-webserver.enabled")
    public static boolean HTTPD_ENABLED;
    @Key("settings.internal-webserver.bind")
    public static String HTTPD_BIND;
    @Key("settings.internal-webserver.port")
    public static int HTTPD_PORT;

    private static final Config CONFIG = new Config();

    public static void reload() {
        Pl3xMap.getInstance().saveDefaultConfig();
        CONFIG.reload(Pl3xMap.getInstance().getDataFolder().toPath().resolve("config.yml"), Config.class);
    }
}
