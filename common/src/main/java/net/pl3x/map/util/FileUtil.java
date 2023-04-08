package net.pl3x.map.util;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.JarURLConnection;
import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.PathMatcher;
import java.nio.file.StandardCopyOption;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.List;
import java.util.stream.Stream;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import net.minecraft.server.level.ServerLevel;
import net.pl3x.map.Pl3xMap;
import net.pl3x.map.logger.Logger;

public class FileUtil {
    public static final Path MAIN_DIR = Pl3xMap.api().getMainDir();
    public static final Path DATA_DIR = FileUtil.MAIN_DIR.resolve("data");
    public static final Path LOCALE_DIR = FileUtil.MAIN_DIR.resolve("locale");

    public static final PathMatcher MCA_MATCHER = FileSystems.getDefault().getPathMatcher("glob:**/*.mca");

    private FileUtil() {
    }

    public static void extract(Class<?> clazz, String filename, String outDir, boolean replace) {
        try (InputStream in = clazz.getResourceAsStream("/" + filename)) {
            if (in == null) {
                throw new RuntimeException("Could not read icon image from jar!");
            }
            Path path = MAIN_DIR.resolve(outDir).resolve(filename);
            if (!Files.exists(path) || replace) {
                Files.copy(in, path, StandardCopyOption.REPLACE_EXISTING);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

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

    public static List<Path> getRegionFiles(ServerLevel level) {
        Path regionDir = level.convertable.getDimensionPath(level.dimension()).resolve("region");
        try (Stream<Path> stream = Files.list(regionDir)) {
            return stream.filter(MCA_MATCHER::matches).toList();
        } catch (IOException e) {
            throw new RuntimeException("Failed to list region files in directory '" + regionDir.toAbsolutePath() + "'", e);
        }
    }

    public static void deleteSubdirectories(Path dir) throws IOException {
        try (Stream<Path> files = Files.list(dir)) {
            files.forEach(path -> {
                try {
                    deleteDirectory(path);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
        }
    }

    public static void deleteDirectory(Path dir) throws IOException {
        try (Stream<Path> walk = Files.walk(dir)) {
            //noinspection ResultOfMethodCallIgnored
            walk.sorted(Comparator.reverseOrder())
                    .map(Path::toFile)
                    .forEach(File::delete);
        }
    }

    public static void write(String str, Path file) {
        try (
                OutputStream fileOut = Files.newOutputStream(mkDirs(file));
                Writer writer = new OutputStreamWriter(fileOut)
        ) {
            writer.write(str);
            writer.flush();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void saveGzip(String json, Path file) throws IOException {
        try (
                OutputStream fileOut = Files.newOutputStream(mkDirs(file));
                GZIPOutputStream gzipOut = new GZIPOutputStream(fileOut);
                Writer writer = new OutputStreamWriter(gzipOut)
        ) {
            writer.write(json);
            writer.flush();
        }
    }

    public static void saveGzip(byte[] bytes, Path file) throws IOException {
        try (
                OutputStream fileOut = Files.newOutputStream(mkDirs(file));
                GZIPOutputStream gzipOut = new GZIPOutputStream(fileOut)
        ) {
            gzipOut.write(bytes);
            gzipOut.flush();
        }
    }

    public static void readGzip(Path file, ByteBuffer buffer) throws IOException {
        try (
                InputStream fileIn = Files.newInputStream(file);
                GZIPInputStream gzipIn = new GZIPInputStream(fileIn)
        ) {
            // try reading all bytes and closing stream _before_ putting into buffer
            byte[] bytes = gzipIn.readAllBytes();
            gzipIn.close();
            buffer.put(bytes);
        }
    }

    public static Path mkDirs(Path path) throws IOException {
        if (!Files.exists(path)) {
            Files.createDirectories(path.getParent());
            Files.createFile(path);
        }
        return path;
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
}
