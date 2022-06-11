package net.pl3x.map.util;

import net.pl3x.map.Pl3xMap;
import net.pl3x.map.configuration.Config;
import net.pl3x.map.logger.Logger;

import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.JarURLConnection;
import java.net.URL;
import java.nio.file.Files;
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
    public static final Path DATA_DIR = PLUGIN_DIR.resolve("data");
    public static final Path LOCALE_DIR = PLUGIN_DIR.resolve("locale");
    public static final Path TILES_DIR = WEB_DIR.resolve("tiles");
    public static final Map<UUID, Path> WORLD_DIRS = new HashMap<>();
    public static final Map<UUID, Path> REGION_DIRS = new HashMap<>();

    public static void extract(String inDir, Path outDir, boolean replace) {
        // https://coderanch.com/t/472574/java/extract-directory-current-jar
        URL dirURL = FileUtil.class.getResource(inDir);
        if (dirURL == null) {
            throw new IllegalStateException("can't find " + inDir + " on the classpath");
        }
        if (!dirURL.getProtocol().equals("jar")) {
            throw new IllegalStateException("don't know how to handle extracting from " + dirURL);
        }
        ZipFile jar;
        try {
            Logger.debug("Extracting " + inDir + " directory from jar...");
            jar = ((JarURLConnection) dirURL.openConnection()).getJarFile();
        } catch (IOException e) {
            Logger.severe("Failed to extract directory from jar", e);
            return;
        }
        String path = inDir.substring(1);
        Enumeration<? extends ZipEntry> entries = jar.entries();
        while (entries.hasMoreElements()) {
            ZipEntry entry = entries.nextElement();
            String name = entry.getName();
            if (!name.startsWith(path)) {
                continue;
            }
            Path file = outDir.resolve(name.substring(path.length()));
            boolean exists = Files.exists(file);
            if (!replace && exists) {
                Logger.debug("  <yellow>exists</yellow>   " + name);
                continue;
            }
            if (entry.isDirectory()) {
                if (!exists) {
                    try {
                        Files.createDirectories(file);
                        Logger.debug("  <green>creating</green> " + name);
                    } catch (IOException e) {
                        Logger.debug("  <red><bold>failed</bold></red>   " + name);
                    }
                } else {
                    Logger.debug("  <yellow>exists</yellow>   " + name);
                }
                continue;
            }
            try (InputStream in = jar.getInputStream(entry); OutputStream out = new BufferedOutputStream(new FileOutputStream(file.toFile()))) {
                byte[] buffer = new byte[4096];
                int readCount;
                while ((readCount = in.read(buffer)) > 0) {
                    out.write(buffer, 0, readCount);
                }
                out.flush();
                Logger.debug("  <green>writing</green>  " + name);
            } catch (IOException e) {
                Logger.debug("  <red><bold>failed</bold></red>   " + name);
                Logger.warn("Failed to extract file (" + name + ") from jar!");
                e.printStackTrace();
            }
        }
    }
}
