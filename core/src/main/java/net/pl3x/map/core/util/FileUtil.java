/*
 * MIT License
 *
 * Copyright (c) 2020 William Blake Galbreath
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
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
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

public class FileUtil {
    public static final PathMatcher MCA_MATCHER = FileSystems.getDefault().getPathMatcher("glob:**/r.*.*.mca");

    public @NonNull
    static Path getTilesDir() {
        return getWebDir().resolve("tiles");
    }

    public static @NonNull Path getWebDir() {
        return Config.WEB_DIR.startsWith("/") ? Path.of(Config.WEB_DIR) : Pl3xMap.api().getMainDir().resolve(Config.WEB_DIR);
    }

    public static void extractFile(@NonNull Class<@NonNull ?> clazz, @NonNull String filename, @NonNull Path outDir, boolean replace) {
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

    public static void extractDir(@NonNull String sourceDir, @NonNull Path outDir, boolean replace) {
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

    public static void openJar(@NonNull Path jar, @NonNull Consumer<@NonNull FileSystem> consumer) throws IOException {
        try (FileSystem fileSystem = FileSystems.newFileSystem(jar)) {
            consumer.accept(fileSystem);
        }
    }

    public static void write(@NonNull String str, @NonNull Path file) {
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

    public static void saveGzip(@NonNull String json, @NonNull Path file) throws IOException {
        try (
                OutputStream fileOut = Files.newOutputStream(mkDirs(file));
                GZIPOutputStream gzipOut = new GZIPOutputStream(fileOut);
                Writer writer = new OutputStreamWriter(gzipOut)
        ) {
            writer.write(json);
            writer.flush();
        }
    }

    public static void saveGzip(byte[] bytes, @NonNull Path file) throws IOException {
        try (
                OutputStream fileOut = Files.newOutputStream(mkDirs(file));
                GZIPOutputStream gzipOut = new GZIPOutputStream(fileOut)
        ) {
            gzipOut.write(bytes);
            gzipOut.flush();
        }
    }

    public static void readGzip(@NonNull Path file, @NonNull ByteBuffer buffer) throws IOException {
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

    public static @NonNull Path mkDirs(@NonNull Path file) throws IOException {
        if (!Files.exists(file)) {
            Files.createDirectories(file.getParent());
            Files.createFile(file);
        }
        return file;
    }

    public static void createDirs(@NonNull Path dirPath) {
        if (!Files.exists(dirPath)) {
            try {
                Files.createDirectories(dirPath);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    public static void deleteDirectory(@NonNull Path dir) throws IOException {
        try (Stream<Path> walk = Files.walk(dir)) {
            walk.sorted(Comparator.reverseOrder())
                    .map(Path::toFile)
                    .forEach(File::delete);
        }
    }

    public static @NonNull Collection<@NonNull Point> regionPathsToPoints(@NonNull World world, @Nullable Collection<@NonNull Path> paths) {
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
                long storedModifiedTime = world.getRegionModifiedState().get(Mathf.asLong(rX, rZ));
                long actualModifiedTime = Files.getLastModifiedTime(file).toMillis();
                if (actualModifiedTime > storedModifiedTime) {
                    Logger.debug("Adding region: " + file.getFileName());
                    regions.add(Point.of(rX, rZ));
                } else {
                    Logger.debug("Skipping unmodified region: " + file.getFileName() + " " + actualModifiedTime + " <= " + storedModifiedTime);
                }
            } catch (NumberFormatException ignore) {
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        return regions;
    }
}
