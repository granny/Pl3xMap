package net.pl3x.map.configuration;

import net.pl3x.map.util.FileUtil;

public class Config extends AbstractConfig {
    @Key("settings.debug-mode")
    @Comment("Extra logger output.\n(can be spammy)")
    public static boolean DEBUG_MODE = false;
    @Key("settings.language-file")
    @Comment("The language file to use in Pl3xMap/locale/ folder.")
    public static String LANGUAGE_FILE = "lang-en.yml";

    @Key("settings.web-directory.path")
    @Comment("The directory that houses the Pl3xMap website and world tiles.")
    public static String WEB_DIR = "web";
    @Key("settings.web-directory.read-only")
    @Comment("Set to true if you don't want Pl3xMap to overwrite\nthe website files on startup. (Good for servers that\ncustomize these files)")
    public static boolean WEB_DIR_READONLY = false;

    @Key("settings.internal-webserver.enabled")
    @Comment("Enable the built-in web server.\nDisable this if you want to use a standalone web server such as apache/nginx.")
    public static boolean HTTPD_ENABLED = true;
    @Key("settings.internal-webserver.bind")
    @Comment("The interface IP the built-in web server should bind to.\nThis is NOT always the same as your public facing IP.\nIf you don't understand what this is, leave it set to 0.0.0.0")
    public static String HTTPD_BIND = "0.0.0.0";
    @Key("settings.internal-webserver.port")
    @Comment("The port the built-in web server listens to.")
    public static int HTTPD_PORT = 8080;

    private static final Config CONFIG = new Config();

    public static void reload() {
        CONFIG.reload(FileUtil.PLUGIN_DIR.resolve("config.yml"), Config.class);
    }
}
