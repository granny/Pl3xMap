package net.pl3x.map.core.util;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.ByteBuffer;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.PathMatcher;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Stream;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;
import net.pl3x.map.core.Pl3xMap;
import net.pl3x.map.core.configuration.Config;
import net.pl3x.map.core.log.Logger;
import net.pl3x.map.core.markers.Point;
import net.pl3x.map.core.world.World;

public class FileUtil {
    public static final PathMatcher MCA_MATCHER = FileSystems.getDefault().getPathMatcher("glob:**/r.*.*.mca");

    public static Path getTilesDir() {
        return getWebDir().resolve("tiles");
    }

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

    public static void extractDir(String sourceDir, Path outDir, boolean replace) {
        Pl3xMap.api().useJar(jar -> {
            try {
                Path inDir = jar.resolve(sourceDir);
                if (!Files.exists(inDir)) {
                    throw new IllegalStateException("can't find " + inDir + " on the classpath");
                }
                Logger.debug("Extracting " + inDir + " directory from jar...");
                try (Stream<Path> stream = Files.walk(inDir)) {
                    stream.forEach(source -> {
                        Path target = outDir.resolve(inDir.relativize(source).toString());
                        String friendlyPathName = inDir.resolve(source).toString();
                        boolean exists = Files.exists(target);
                        if (Files.isDirectory(source)) {
                            if (!exists) {
                                try {
                                    Logger.debug("  <green>creating</green> " + friendlyPathName);
                                    Files.createDirectories(target);
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }
                            return;
                        }
                        if (!replace && exists) {
                            Logger.debug("  <yellow>exists</yellow>   " + friendlyPathName);
                            return;
                        }
                        try {
                            Logger.debug("  <green>writing</green>  " + friendlyPathName);
                            Files.copy(source, target, StandardCopyOption.REPLACE_EXISTING);
                        } catch (IOException e) {
                            Logger.debug("  <red><bold>failed</bold></red>   " + friendlyPathName);
                            Logger.warn("Failed to extract file (" + friendlyPathName + ") from jar!");
                            e.printStackTrace();
                        }
                    });
                }
            } catch (IOException e) {
                Logger.warn("Failed to extract file (" + sourceDir + ") from jar!");
                e.printStackTrace();
            }
        });
    }

    public static void openJar(Path jar, Consumer<FileSystem> consumer) throws IOException {
        try (FileSystem fileSystem = FileSystems.newFileSystem(jar)) {
            consumer.accept(fileSystem);
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

    public static Path mkDirs(Path file) throws IOException {
        if (!Files.exists(file)) {
            Files.createDirectories(file.getParent());
            Files.createFile(file);
        }
        return file;
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

    @SuppressWarnings("ResultOfMethodCallIgnored")
    public static void deleteDirectory(Path dir) throws IOException {
        try (Stream<Path> walk = Files.walk(dir)) {
            walk.sorted(Comparator.reverseOrder())
                    .map(Path::toFile)
                    .forEach(File::delete);
        }
    }

    public static Collection<Point> regionPathsToPoints(World world, Collection<Path> paths) {
        if (paths == null || paths.isEmpty()) {
            return Collections.emptyList();
        }
        List<Point> regions = new ArrayList<>();
        for (Path file : paths) {
            if (file.toFile().length() <= 0) {
                Logger.debug("Skipping zero length region: " + file.getFileName());
                continue;
            }
            try {
                String[] split = file.getFileName().toString().split("\\.");
                int rX = Integer.parseInt(split[1]);
                int rZ = Integer.parseInt(split[2]);
                long modified = world.getRegionModifiedState().get(Mathf.asLong(rX, rZ));
                if (Files.getLastModifiedTime(file).toMillis() > modified) {
                    Logger.debug("Adding region: " + file.getFileName());
                    regions.add(Point.of(rX, rZ));
                } else {
                    Logger.debug("Skipping unmodified region: " + file.getFileName());
                }
            } catch (NumberFormatException ignore) {
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        return regions;
    }
}
