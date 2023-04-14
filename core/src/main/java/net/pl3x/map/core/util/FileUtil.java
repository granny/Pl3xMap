package net.pl3x.map.core.util;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.JarURLConnection;
import java.net.URL;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.PathMatcher;
import java.nio.file.StandardCopyOption;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.stream.Stream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import net.pl3x.map.core.Pl3xMap;
import net.pl3x.map.core.configuration.Config;
import net.pl3x.map.core.log.Logger;

public class FileUtil {
    public static final PathMatcher MCA_MATCHER = FileSystems.getDefault().getPathMatcher("glob:**/r.*.*.mca");

    public static Path getWebDir() {
        return Config.WEB_DIR.startsWith("/") ? Path.of(Config.WEB_DIR) : Pl3xMap.api().getMainDir().resolve(Config.WEB_DIR);
    }

    public static void extractFile(Class<?> clazz, String filename, Path outDir, boolean replace) {
        try (InputStream in = clazz.getResourceAsStream("/" + filename)) {
            if (in == null) {
                throw new RuntimeException("Could not read file from jar! (" + filename + ")");
            }
            Path path = outDir.resolve(filename);
            if (!Files.exists(path) || replace) {
                Files.copy(in, path, StandardCopyOption.REPLACE_EXISTING);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void extractDir(String inDir, Path outDir, boolean replace) {
        // https://coderanch.com/t/472574
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
            Logger.severe("Failed to extract directory from jar");
            throw new RuntimeException(e);
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

    public static void createDirs(Path dirPath) {
        if (!Files.exists(dirPath)) {
            try {
                Files.createDirectories(dirPath);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public static void deleteDirectory(Path dir) throws IOException {
        try (Stream<Path> walk = Files.walk(dir)) {
            walk.sorted(Comparator.reverseOrder())
                    .map(Path::toFile)
                    .forEach(File::delete);
        }
    }
}
