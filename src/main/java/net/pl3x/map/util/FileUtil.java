package net.pl3x.map.util;

import net.pl3x.map.Pl3xMap;
import net.pl3x.map.configuration.Config;
import net.pl3x.map.logger.Logger;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.JarURLConnection;
import java.net.URL;
import java.nio.file.Path;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class FileUtil {
    public static final Path PLUGIN_DIR = Pl3xMap.getInstance().getDataFolder().toPath();
    public static final Path WEB_DIR = PLUGIN_DIR.resolve(Config.WEB_DIR);
    public static final Path LOCALE_DIR = PLUGIN_DIR.resolve("locale");
    public static final Path TILES_DIR = WEB_DIR.resolve("tiles");
    public static final Map<UUID, Path> WORLD_DIRS = new HashMap<>();
    public static final Map<UUID, Path> REGION_DIRS = new HashMap<>();

    public static void extract(String inDir, File outDir, boolean replace) {
        // https://coderanch.com/t/472574/java/extract-directory-current-jar
        URL dirURL = FileUtil.class.getResource(inDir);
        String path = inDir.substring(1);
        if ((dirURL != null) && dirURL.getProtocol().equals("jar")) {
            ZipFile jar;
            try {
                Logger.debug("Extracting " + inDir + " directory from jar...");
                jar = ((JarURLConnection) dirURL.openConnection()).getJarFile();
            } catch (IOException e) {
                Logger.severe("Failed to extract directory from jar", e);
                return;
            }
            Enumeration<? extends ZipEntry> entries = jar.entries();
            while (entries.hasMoreElements()) {
                ZipEntry entry = entries.nextElement();
                String name = entry.getName();
                if (!name.startsWith(path)) {
                    continue;
                }
                File file = new File(outDir, name.substring(path.length()));
                if (!replace && file.exists()) {
                    Logger.debug("  <yellow>exists</yellow>   " + name);
                    continue;
                }
                if (entry.isDirectory()) {
                    if (!file.exists()) {
                        Logger.debug((file.mkdirs() ? "  <green>creating</green> " : "  <red>unable to create</red> ") + name);
                    } else {
                        Logger.debug("  <yellow>exists</yellow>   " + name);
                    }
                } else {
                    Logger.debug("  <green>writing</green>  " + name);
                    try {
                        InputStream in = jar.getInputStream(entry);
                        OutputStream out = new BufferedOutputStream(new FileOutputStream(file));
                        byte[] buffer = new byte[4096];
                        int readCount;
                        while ((readCount = in.read(buffer)) > 0) {
                            out.write(buffer, 0, readCount);
                        }
                        out.flush();
                        out.close();
                        in.close();
                    } catch (IOException e) {
                        Logger.severe("Failed to extract file (" + name + ") from jar!", e);
                    }
                }
            }
        } else if (dirURL == null) {
            throw new IllegalStateException("can't find " + inDir + " on the classpath");
        } else {
            throw new IllegalStateException("don't know how to handle extracting from " + dirURL);
        }
    }
}
